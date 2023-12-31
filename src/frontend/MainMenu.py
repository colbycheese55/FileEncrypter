import customtkinter as ctk


root = ctk.CTk()
root.title("File Encrypter Menu")
padding = 10
vault = None

# Functions
def systemHandler(command: str) -> None:
    saveFlag = saveOptions.get()
    match saveFlag:
        case "Add and Save":
            vault.saveFiles(True)
        case "Save Existing Only":
            vault.saveFiles(False)
        case "Don't Save":
            pass
        case _:
            raise Exception
    deleteFlag = deleteOptions.get()
    match deleteFlag:
        case "Remove from this user":
            vault.removeFiles(False)
        case "Delete from all users":
            vault.removeFiles(True)
        case "Don't Remove":
            pass
        case _:
            raise Exception
    match command:
        case "refresh":
            updateFilesList()
        case "logout":
            root.destroy()
        case "quit":
            root.destroy()
        case _:
            raise Exception
        

def updateFilesList() -> None:
    fileListing = vault.listFiles()
    info = vault.getVaultInfo()

    textBox.configure(state=ctk.NORMAL)
    textBox.delete("1.0", ctk.END)
    textBox.insert(ctk.END, fileListing)
    textBox.configure(state=ctk.DISABLED)
    infoLabel.configure(text=info)
#root.after(1, updateFilesList)
    


# Text box and Labels
labelFont = ("Franklin Gothic Heavy", 20)
textFont = ("Calibri", 16)

textBoxLabel = ctk.CTkLabel(root, text="Registered Files", font=labelFont)
textBoxLabel.grid(row=0, rowspan=1, column=0, columnspan=3, padx=padding, pady=padding)

optionsLabel = ctk.CTkLabel(root, text="Options", font=labelFont)
optionsLabel.grid(row=0, rowspan=1, column=4, columnspan=1, padx=padding, pady=padding)

infoLabel = ctk.CTkLabel(root, text="PLACEHOLDER", font=textFont)
infoLabel.grid(row=6, rowspan=1, column=0, columnspan=3, padx=padding, pady=0)


textBox = ctk.CTkTextbox(root, width=300, height=300, font=textFont)
textBox.grid(row=1, rowspan=5, column=0, columnspan=3, padx=padding, pady=padding)
textBox.configure(state=ctk.DISABLED)


# Buttons
btnParams = {"font": labelFont, "width": 100, "height": 30}

refreshBtn = ctk.CTkButton(root, text="Refresh", **btnParams, command=lambda: systemHandler("refresh"))
refreshBtn.grid(row=1, rowspan=1, column=4, columnspan=1, padx=padding)

logoutBtn = ctk.CTkButton(root, text="Logout", **btnParams, command=lambda: systemHandler("logout"))
logoutBtn.grid(row=2, rowspan=1, column=4, columnspan=1, padx=padding)

quitBtn = ctk.CTkButton(root, text="Quit", **btnParams, command=lambda: systemHandler("quit"))
quitBtn.grid(row=3, rowspan=1, column=4, columnspan=1, padx=padding)

shareBtn = ctk.CTkButton(root, text="Share", **btnParams)
shareBtn.grid(row=7, rowspan=1, column=0, columnspan=3, pady=padding)

saveOptions = ctk.CTkComboBox(root, values=("Add and Save", "Save Existing Only", "Don't Save"), font=textFont, width=170)
saveOptions.grid(row=4, rowspan=1, column=4, columnspan=1, padx=padding)

deleteOptions = ctk.CTkComboBox(root, values=("Remove from this user", "Delete from all users", "Don't Remove"), font=textFont, width=170)
deleteOptions.grid(row=5, rowspan=1, column=4, columnspan=1, padx=padding)



def openMenu(vaultObj, FileHandler):
    global vault
    vault = vaultObj
    root.mainloop()

    FileHandler.closeVault()