import customtkinter as ctk
from tkinter import messagebox as mb
import jpype as java
from Standards import padding, textFont, labelFont, entryFont, linkFont, largeBtnParams, smallBtnParams
from ShareMenu import runShareReceivedMenu, runShareSendMenu


def runMainMenu(user, userManager, path: str, classes: dict) -> bool:
        menu = MainMenu(user, userManager, path, classes)
        menu.shareReceiveHandler(checkOnly=True)
        menu.root.after(500, menu.openVault)
        menu.root.mainloop()
        classes["FileHandler"].closeVault()
        return menu.rerun


class MainMenu:
    def __init__(this, user, userManager, path: str, classes: dict) -> None:
        this.root = ctk.CTk()
        this.root.title("File Encrypter Menu")
        this.root.geometry("510x520")
        this.root.resizable(False, False)
        this.root.protocol("WM_DELETE_WINDOW", lambda: this.systemHandler("quit"))
        
        # Text box and Labels
        this.textBoxLabel = ctk.CTkLabel(this.root, text="Registered Files", font=labelFont)
        this.textBoxLabel.grid(row=0, rowspan=1, column=0, columnspan=3, padx=padding, pady=padding)

        this.optionsLabel = ctk.CTkLabel(this.root, text="Options", font=labelFont)
        this.optionsLabel.grid(row=0, rowspan=1, column=4, columnspan=1, padx=padding, pady=padding)

        this.infoLabel = ctk.CTkLabel(this.root, text="PLACEHOLDER", font=textFont)
        this.infoLabel.grid(row=6, rowspan=1, column=0, columnspan=3, padx=padding, pady=0)

        this.textBox = ctk.CTkTextbox(this.root, width=300, height=300, font=textFont)
        this.textBox.grid(row=1, rowspan=5, column=0, columnspan=3, padx=padding, pady=padding)
        this.textBox.configure(state=ctk.DISABLED)

        # Buttons
        this.refreshBtn = ctk.CTkButton(this.root, text="Refresh", **smallBtnParams, command=lambda: this.systemHandler("refresh"))
        this.refreshBtn.grid(row=1, rowspan=1, column=4, columnspan=1, padx=padding)

        this.logoutBtn = ctk.CTkButton(this.root, text="Logout", **smallBtnParams, command=lambda: this.systemHandler("logout"))
        this.logoutBtn.grid(row=2, rowspan=1, column=4, columnspan=1, padx=padding)

        this.quitBtn = ctk.CTkButton(this.root, text="Quit", **smallBtnParams, command=lambda: this.systemHandler("quit"))
        this.quitBtn.grid(row=3, rowspan=1, column=4, columnspan=1, padx=padding)

        this.shareSendBtn = ctk.CTkButton(this.root, text="Send Files", **largeBtnParams, command=lambda: runShareSendMenu(this.vault, path, classes["User"]))
        this.shareSendBtn.grid(row=7, rowspan=1, column=0, columnspan=2, pady=padding)

        this.shareReceiveBtn = ctk.CTkButton(this.root, text="PLACEHOLDER", **largeBtnParams, command=this.shareReceiveHandler)
        this.shareReceiveBtn.grid(row=7, rowspan=1, column=2, columnspan=3)

        this.saveOptions = ctk.CTkComboBox(this.root, values=("Add and Save", "Save Existing Only", "Don't Save"), font=textFont, width=170)
        this.saveOptions.grid(row=4, rowspan=1, column=4, columnspan=1, padx=padding)

        this.deleteOptions = ctk.CTkComboBox(this.root, values=("Remove from this user", "Delete from all users", "Don't Remove"), font=textFont, width=170)
        this.deleteOptions.grid(row=5, rowspan=1, column=4, columnspan=1, padx=padding)

        # Progress Bar
        this.progressBarLabel = ctk.CTkLabel(this.root, text="", font=labelFont)
        this.progressBarLabel.grid(row=8, rowspan=1, column=0, columnspan=5, pady=padding)

        this.progressBar = ctk.CTkProgressBar(this.root, orientation="horizontal", width=400, progress_color="#CD5250")

        # Handling other variables
        PBpy = ProgressBarManager(this.progressBar, this.progressBarLabel, this.root)
        this.PBmanager = java.JProxy(classes["ProgressBar"], inst=PBpy)
        this.vault = classes["Vault"](userManager, user, this.PBmanager)
        this.user = user
        this.classes = classes
        this.rerun = False


    def openVault(this) -> None:
        missingFiles = this.classes["FileHandler"].openVault(this.user, this.PBmanager)
        if len(missingFiles) > 0:
            text = "The following files are missing, and are at risk of being removed: \n" + "\n".join(missingFiles)
            mb.showwarning("Missing Files", text)

    def systemHandler(this, command: str) -> None:
        saveFlag = this.saveOptions.get()
        match saveFlag:
            case "Add and Save":
                this.vault.saveFiles(True)
            case "Save Existing Only":
                this.vault.saveFiles(False)
            case "Don't Save":
                pass
            case _:
                raise Exception()
        deleteFlag = this.deleteOptions.get()
        match deleteFlag:
            case "Remove from this user":
                this.vault.removeFiles(False)
            case "Delete from all users":
                this.vault.removeFiles(True)
            case "Don't Remove":
                pass
            case _:
                raise Exception()
        match command:
            case "refresh":
                this.updateFilesList()
            case "logout":
                this.rerun = True
                this.root.destroy()
            case "quit":
                this.rerun = False
                this.root.destroy()
            case _:
                raise Exception()
        
    def updateFilesList(this) -> None:
        fileListing = this.vault.listFiles()
        info = this.vault.getVaultInfo()

        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.insert(ctk.END, fileListing)
        this.textBox.configure(state=ctk.DISABLED)
        this.infoLabel.configure(text=info)

    def shareReceiveHandler(this, checkOnly=False) -> None:
        if checkOnly is False:
            runShareReceivedMenu(this.vault)
        numOfFiles = this.vault.numSharedFiles()
        this.shareReceiveBtn.configure(text=f"Inbox ({numOfFiles})")
        if numOfFiles == 0:
            this.shareReceiveBtn.configure(state=ctk.DISABLED)
        this.updateFilesList()


class ProgressBarManager():
    def __init__(this, progressBar: ctk.CTkProgressBar, label: ctk.CTkLabel, root: ctk.CTk) -> None:
        this.progressBar = progressBar
        this.label = label
        this.root = root

    def start(this) -> None:
        this.label.configure(text="Progress Bar")
        this.progressBar.grid(row=9, rowspan=1, column=0, columnspan=5)  
        this.progressBar.set(0) 
        this.root.update_idletasks()     

    def update(this, val: float) -> None:
        if val > 1 or val < 0:
            raise Exception("val must be within [0,1]")
        this.progressBar.set(val)
        this.root.update_idletasks()

    def complete(this) -> None:
        this.label.configure(text="")
        this.progressBar.grid_forget()