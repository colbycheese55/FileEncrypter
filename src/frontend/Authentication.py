import customtkinter as ctk


class AuthWindow:
    def __init__(this, userManager, UserClass) -> None:
        this.authUser = None
        this.userManager = userManager
        this.UserClass = UserClass
        this.onClick = this.enterCredentials
        padding = 10
        font = ("Calibri", 18)

        this.root = ctk.CTk()
        this.root.title("Authentication")
        this.root.geometry("250x400")


        # Row Centering
        this.root.columnconfigure(0, weight=1)
        this.root.columnconfigure(1, weight=0)
        this.root.columnconfigure(2, weight=1)


        # Elements 
        this.instructions = ctk.CTkLabel(this.root, text="Enter Credentials", font=font)
        this.instructions.grid(row=0, rowspan=1, pady=padding, column=0, columnspan=3)

        this.usernameEntry = ctk.CTkEntry(this.root, placeholder_text="Username", width=200, height=40, font=font)
        this.usernameEntry.grid(row=2, rowspan=1, pady=padding, column=0, columnspan=3)

        this.passwordEntry = ctk.CTkEntry(this.root, placeholder_text="Password", width=200, height=40, font=font)
        this.passwordEntry.grid(row=3, rowspan=1, pady=padding, column=0, columnspan=3)

        this.enterBtn = ctk.CTkButton(this.root, text="Login", font=("Franklin Gothic Heavy", 20), command=this.onClick, width=100, height=30)
        this.enterBtn.grid(row=4, rowspan=1, column=0, columnspan=3)

        emptyRow = ctk.CTkLabel(this.root, text="", height=150)
        emptyRow.grid(row=5, rowspan=1, column=0, columnspan=3)

        this.newUserLabel = ctk.CTkLabel(this.root, text="Create a new user", font=font)
        this.newUserLabel.grid(row=6, rowspan=1, column=0, columnspan=3)
        this.newUserLabel.bind("<Button-1>", lambda _: this.setupcreateNewUser())


        # Keybinds
        this.root.bind("<Return>", lambda _: this.onClick())
        this.root.bind("<Control-w>", lambda _: this.root.destroy())


    def authenticate(this) -> any:
        this.root.mainloop()
        return this.authUser


    def setupcreateNewUser(this) -> None:
        this.instructions.configure(text="Enter New Credentials")
        this.enterBtn.configure(text="Create")
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


    def enterCredentials(this) -> None:
        username = this.usernameEntry.get()
        password = this.passwordEntry.get()
        result = this.UserClass.authenticate(username, password, this.userManager)
        if result:
            this.authUser = result
            this.root.destroy()
        else:
            this.instructions.configure(text="Invalid Credentials!")