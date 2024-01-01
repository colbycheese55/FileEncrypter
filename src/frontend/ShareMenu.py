import customtkinter as ctk
from Standards import padding, textFont, labelFont, entryFont, linkFont, largeBtnParams, smallBtnParams


def runShareReceivedMenu(vault) -> None:
    menu = ShareReceivedMenu(vault)
    menu.tryOTP(vault.getUser().getName())
    menu.root.mainloop()


class ShareReceivedMenu:
    def __init__(this, vault) -> None:
        this.vault = vault
        this.root = ctk.CTk()
        this.root.title("Received Files")
        this.root.geometry("480x320")
        this.root.resizable(False, False)

        this.textBox = ctk.CTkTextbox(this.root, width=300, height=300, font=textFont)
        this.textBox.grid(row=1, rowspan=5, column=0, columnspan=3, padx=padding, pady=padding)
        this.textBox.configure(state=ctk.DISABLED)

        this.otpEntry = ctk.CTkEntry(this.root, font=entryFont, placeholder_text="Next OTP")
        this.otpEntry.grid(row=1, rowspan=1, column=4, columnspan=1)

        this.tryBtn = ctk.CTkButton(this.root, text="Try OTP", command=this.tryOTP, **smallBtnParams)
        this.tryBtn.grid(row=2, rowspan=1, column=4, columnspan=1)

        this.info = ctk.CTkLabel(this.root, text="", font=textFont)
        this.info.grid(row=3, rowspan=1, column=4, columnspan=1)

        this.root.bind("<Return>", lambda _: this.tryOTP(None))


    def tryOTP(this, password=None) -> None:
        password = this.otpEntry.get() if password is None else password
        text = this.vault.trySharedFiles(password)
        text = f"Files unlocked by \"{password}\": \n{text}"
        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.insert(ctk.END, text)
        this.textBox.configure(state=ctk.DISABLED)
        filesLeftText = f"{this.vault.numSharedFiles()} File(s) still locked"
        this.info.configure(text=filesLeftText)


def runShareSendMenu():
    pass


class ShareSendMenu:
    pass
