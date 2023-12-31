import customtkinter as ctk


def runMainMenu(vaultObj, FileHandler) -> bool:
        menu = MainMenu(vaultObj)
        menu.updateFilesList()
        menu.root.mainloop()
        FileHandler.closeVault()
        return menu.rerun


class MainMenu:
    def __init__(this, vaultObj) -> None:
        this.root = ctk.CTk()
        this.root.title("File Encrypter Menu")
        this.root.protocol("WM_DELETE_WINDOW", lambda: this.systemHandler("quit"))
        
        this.vault = vaultObj
        this.rerun = False
        padding = 10

        
        # Text box and Labels
        labelFont = ("Franklin Gothic Heavy", 20)
        textFont = ("Calibri", 16)

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
        btnParams = {"font": labelFont, "width": 100, "height": 30}

        this.refreshBtn = ctk.CTkButton(this.root, text="Refresh", **btnParams, command=lambda: this.systemHandler("refresh"))
        this.refreshBtn.grid(row=1, rowspan=1, column=4, columnspan=1, padx=padding)

        this.logoutBtn = ctk.CTkButton(this.root, text="Logout", **btnParams, command=lambda: this.systemHandler("logout"))
        this.logoutBtn.grid(row=2, rowspan=1, column=4, columnspan=1, padx=padding)

        this.quitBtn = ctk.CTkButton(this.root, text="Quit", **btnParams, command=lambda: this.systemHandler("quit"))
        this.quitBtn.grid(row=3, rowspan=1, column=4, columnspan=1, padx=padding)

        this.shareBtn = ctk.CTkButton(this.root, text="Share", **btnParams)
        this.shareBtn.grid(row=7, rowspan=1, column=0, columnspan=3, pady=padding)

        this.saveOptions = ctk.CTkComboBox(this.root, values=("Add and Save", "Save Existing Only", "Don't Save"), font=textFont, width=170)
        this.saveOptions.grid(row=4, rowspan=1, column=4, columnspan=1, padx=padding)

        this.deleteOptions = ctk.CTkComboBox(this.root, values=("Remove from this user", "Delete from all users", "Don't Remove"), font=textFont, width=170)
        this.deleteOptions.grid(row=5, rowspan=1, column=4, columnspan=1, padx=padding)


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
                raise Exception
        deleteFlag = this.deleteOptions.get()
        match deleteFlag:
            case "Remove from this user":
                this.vault.removeFiles(False)
            case "Delete from all users":
                this.vault.removeFiles(True)
            case "Don't Remove":
                pass
            case _:
                raise Exception
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
                raise Exception
        

    def updateFilesList(this) -> None:
        fileListing = this.vault.listFiles()
        info = this.vault.getVaultInfo()

        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.insert(ctk.END, fileListing)
        this.textBox.configure(state=ctk.DISABLED)
        this.infoLabel.configure(text=info)