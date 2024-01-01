import customtkinter as ctk
from Standards import padding, textFont, labelFont, entryFont, linkFont, largeBtnParams, smallBtnParams


def authenticate(userManager, UserClass) -> any:
        authWindow = AuthWindow(userManager, UserClass)
        authWindow.root.mainloop()
        return authWindow.authUser

class AuthWindow:
    def __init__(this, userManager, UserClass) -> None:
        this.authUser = None
        this.userManager = userManager
        this.UserClass = UserClass
        this.onClick = this.enterCredentials

        this.root = ctk.CTk()
        this.root.title("Authentication")
        this.root.geometry("250x400")
        this.root.resizable(False, False)


        # Row Centering
        this.root.columnconfigure(0, weight=1)
        this.root.columnconfigure(1, weight=0)
        this.root.columnconfigure(2, weight=1)


        # Elements 
        this.instructions = ctk.CTkLabel(this.root, text="Enter Credentials", font=entryFont)
        this.instructions.grid(row=0, rowspan=1, pady=padding, column=0, columnspan=3)

        this.usernameEntry = ctk.CTkEntry(this.root, placeholder_text="Username", width=200, height=40, font=entryFont)
        this.usernameEntry.grid(row=2, rowspan=1, pady=padding, column=0, columnspan=3)

        this.passwordEntry = ctk.CTkEntry(this.root, placeholder_text="Password", width=200, height=40, font=entryFont)
        this.passwordEntry.grid(row=3, rowspan=1, pady=padding, column=0, columnspan=3)

        this.enterBtn = ctk.CTkButton(this.root, text="Login", command=this.onClick, **smallBtnParams)
        this.enterBtn.grid(row=6, rowspan=1, column=0, columnspan=3)

        emptyRow = ctk.CTkLabel(this.root, text="", height=140)
        emptyRow.grid(row=7, rowspan=1, column=0, columnspan=3)

        this.newUserLabel = ctk.CTkLabel(this.root, text="Create a New User", font=linkFont)
        this.newUserLabel.grid(row=8, rowspan=1, column=0, columnspan=3)
        this.newUserLabel.bind("<Button-1>", lambda _: this.setupcreateNewUser())

        this.modifyLabel = ctk.CTkLabel(this.root, text="Modify Credentials", font=linkFont)
        this.modifyLabel.grid(row=9, rowspan=1, column=0, columnspan=3)
        this.modifyLabel.bind("<Button-1>", lambda _: this.setupModifyCredentials())


        # Keybinds
        this.root.bind("<Return>", lambda _: this.onClick())
        this.root.bind("<Control-w>", lambda _: this.root.destroy())


    def setupcreateNewUser(this) -> None:
        this.instructions.configure(text="Enter New Credentials")
        this.enterBtn.configure(text="Create")
        this.newUserLabel.configure(text="")
        this.modifyLabel.configure(text="")
        this.onClick = this.createNewUser
    def createNewUser(this) -> None:
        username = this.usernameEntry.get()
        password = this.passwordEntry.get()
        result = this.userManager.createNewUser(username, password)
        if result:
            this.authUser = this.UserClass.authenticate(username, password, this.userManager)
            this.root.destroy()
        else:
            this.instructions.configure(text="User Already Exists!")
            this.colorChange(this.usernameEntry)


    def enterCredentials(this) -> None:
        username = this.usernameEntry.get()
        password = this.passwordEntry.get()
        result = this.UserClass.authenticate(username, password, this.userManager)
        if result:
            this.authUser = result
            this.root.destroy()
        else:
            this.instructions.configure(text="Invalid Credentials!")
            this.colorChange(this.usernameEntry, this.passwordEntry)
    

    def setupModifyCredentials(this) -> None:
        this.instructions.configure(text="Enter New and Old Credentials")
        this.enterBtn.configure(text="Modify")
        this.newUserLabel.configure(text="")
        this.modifyLabel.configure(text="")
        this.onClick = this.modifyCredentials

        this.newUsernameEntry = ctk.CTkEntry(this.root, placeholder_text="New Username", width=200, height=40, font=entryFont)
        this.newUsernameEntry.grid(row=4, rowspan=1, pady=padding, column=0, columnspan=3)

        this.newPasswordEntry = ctk.CTkEntry(this.root, placeholder_text="New Password", width=200, height=40, font=entryFont)
        this.newPasswordEntry.grid(row=5, rowspan=1, pady=padding, column=0, columnspan=3)

        this.usernameEntry.configure(placeholder_text="Old Username")
        this.passwordEntry.configure(placeholder_text="Old Password")
    def modifyCredentials(this) -> None:
        oldUsername = this.usernameEntry.get()
        oldPassword = this.passwordEntry.get()
        newUsername = this.newUsernameEntry.get()
        newPassword = this.newPasswordEntry.get()
        user = this.UserClass.authenticate(oldUsername, oldPassword, this.userManager)
        if user is None:
            this.instructions.configure(text="Invalid Credentials!")
            this.colorChange(this.usernameEntry, this.passwordEntry)
            return
        success = user.changeCredentials(newUsername, newPassword, this.userManager)
        if success is False:
            this.instructions.configure(text="User Already Exists!")
            this.colorChange(this.newUsernameEntry)
            return
        this.authUser = user
        this.root.destroy()


    def colorChange(this, *elements) -> None:
        for element in elements:
            current = element.cget("fg_color")
            element.configure(fg_color="#CD5250")
            element.after(4000, lambda e=element, c=current: e.configure(fg_color=c))