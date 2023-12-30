import customtkinter as ctk


root = ctk.CTk()
root.title("File Encrypter Menu")
padding = 10

# Functions



# Text box and Labels
labelFont = ("Franklin Gothic Heavy", 20)
textFont = ("Calibri", 16)

textBoxLabel = ctk.CTkLabel(root, text="Registered Files", font=labelFont)
textBoxLabel.grid(row=0, rowspan=1, column=0, columnspan=3, padx=padding, pady=padding)

optionsLabel = ctk.CTkLabel(root, text="Options", font=labelFont)
optionsLabel.grid(row=0, rowspan=1, column=4, columnspan=1, padx=padding, pady=padding)

infoLabel = ctk.CTkLabel(root, text="USER is logged in with 0 file(s) available", font=textFont)
infoLabel.grid(row=6, rowspan=1, column=0, columnspan=3, padx=padding, pady=0)


textBox = ctk.CTkTextbox(root, width=300, height=300, font=textFont)
textBox.grid(row=1, rowspan=5, column=0, columnspan=3, padx=padding, pady=padding)
textBox.configure(state=ctk.DISABLED)


# Buttons
btnParams = {"font": labelFont, "width": 100, "height": 30}

refreshBtn = ctk.CTkButton(root, text="Refresh", **btnParams)
refreshBtn.grid(row=1, rowspan=1, column=4, columnspan=1, padx=padding)

logoutBtn = ctk.CTkButton(root, text="Logout", **btnParams)
logoutBtn.grid(row=2, rowspan=1, column=4, columnspan=1, padx=padding)

quitBtn = ctk.CTkButton(root, text="Quit", **btnParams)
quitBtn.grid(row=3, rowspan=1, column=4, columnspan=1, padx=padding)

shareBtn = ctk.CTkButton(root, text="Share", **btnParams)
shareBtn.grid(row=7, rowspan=1, column=0, columnspan=3, pady=padding)

saveOptions = ctk.CTkComboBox(root, values=("Add and Save", "Save Existing Only", "Don't Save"), font=textFont, width=170)
saveOptions.grid(row=4, rowspan=1, column=4, columnspan=1, padx=padding)

deleteOptions = ctk.CTkComboBox(root, values=("Remove from this user", "Delete from all users", "Don't Remove"), font=textFont, width=170)
deleteOptions.grid(row=5, rowspan=1, column=4, columnspan=1, padx=padding)




root.mainloop()