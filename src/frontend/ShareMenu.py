import customtkinter as ctk
from tkinter import filedialog as fd, messagebox as mb
import os
from Standards import padding, textFont, labelFont, entryFont, linkFont, largeBtnParams, smallBtnParams
from Authentication import authenticate


def runShareReceivedMenu(vault) -> None:
    menu = ShareReceivedMenu(vault)
    menu.tryOTP(vault.getUser().getName())
    menu.root.wait_window()


class ShareReceivedMenu:
    def __init__(this, vault) -> None:
        this.vault = vault
        this.root = ctk.CTkToplevel()
        this.root.attributes("-topmost", True)
        this.root.after(1000, lambda: this.root.attributes("-topmost", False))
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

    def tryOTP(this, username=None) -> None:
        password = username if username else this.otpEntry.get()
        text = "Files unlocked by username:" if username else f"Files unlocked by \"{password}\":"
        content = this.vault.trySharedFiles(password)
        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.insert(ctk.END, f"{text}\n{content}")
        this.textBox.configure(state=ctk.DISABLED)
        filesLeftText = f"{this.vault.numSharedFiles()} File(s) still locked"
        this.info.configure(text=filesLeftText)


def runShareSendMenu(vault, path: str, UserClass):
    menu = ShareSendMenu(vault, path, UserClass)
    menu.root.mainloop()


class ShareSendMenu:
    def __init__(this, vault, path: str, UserClass) -> None:
        this.vault = vault
        this.path = path
        this.UserClass = UserClass
        this.files = set()
        this.root = ctk.CTkToplevel()
        this.root.attributes("-topmost", True)
        this.root.after(1000, lambda: this.root.attributes("-topmost", False))
        this.root.title("Send Files")
        this.root.geometry("500x350")
        this.root.resizable(False, False)

        this.textBoxLabel = ctk.CTkLabel(this.root, text="Files to Share", font=labelFont)
        this.textBoxLabel.grid(row=1, rowspan=1, column=1, columnspan=1)

        this.textBox = ctk.CTkTextbox(this.root, width=300, height=250, font=textFont)
        this.textBox.grid(row=2, rowspan=4, column=1, columnspan=1, padx=padding, pady=padding)
        this.textBox.configure(state=ctk.DISABLED)

        this.chooseFilesBtn = ctk.CTkButton(this.root, text="Choose Files", **largeBtnParams, command=this.chooseFiles)
        this.chooseFilesBtn.grid(row=2, rowspan=1, column=2, columnspan=1)

        this.clearBtn = ctk.CTkButton(this.root, text="Clear Files", **largeBtnParams, command=this.clear)
        this.clearBtn.grid(row=3, rowspan=1, column=2, columnspan=1)

        this.shareMethod = ctk.CTkComboBox(this.root, values=("Share by Login", "Share by username", "Share by OTP"), font=textFont, width=170, command=this.shareMethodSwitch)
        this.shareMethod.grid(row=4, rowspan=1, column=2, columnspan=1)

        this.otpEntry = ctk.CTkEntry(this.root, placeholder_text="Enter the OTP", font=entryFont, width=170)
        this.otpEntry.grid(row=5, rowspan=1, column=2, columnspan=1, pady=padding)
        this.otpEntry.configure(state=ctk.DISABLED)

        this.userEntryBox = ctk.CTkEntry(this.root, placeholder_text="Receiving Username", font=entryFont, width=170)
        this.userEntryBox.grid(row=6, rowspan=1, column=2, columnspan=1, pady=padding)

        this.shareBtn = ctk.CTkButton(this.root, text="Share Files", **largeBtnParams, command=this.share)
        this.shareBtn.grid(row=6, rowspan=1, column=1, columnspan=1)

    def chooseFiles(this) -> None: #TODO, fix allows repeats
        newFiles = fd.askopenfilenames(title="Choose files to share", initialdir=f"{this.path}/unlocked-storage")
        this.files.update(newFiles)
        text = "\n".join([os.path.basename(fileName) for fileName in this.files])

        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.insert(ctk.END, text)
        this.textBox.configure(state=ctk.DISABLED)

    def clear(this) -> None:
        this.files.clear()
        this.textBox.configure(state=ctk.NORMAL)
        this.textBox.delete("1.0", ctk.END)
        this.textBox.configure(state=ctk.DISABLED)

    def share(this) -> None:
        val = this.shareMethod.get()
        match (val):
            case "Share by Login":
                otherUser = authenticate(this.vault.getUserManager(), this.UserClass)
                if otherUser is None:
                    return 
                this.vault.shareNow(otherUser, list(this.files))
            case "Share by username":
                receivingUser = this.userEntryBox.get()
                result = this.vault.shareEncrypted(receivingUser, receivingUser, list(this.files))
                if result is False:
                    mb.showerror("User Doesn't Exist", f"The user \"{receivingUser}\" does not exist!")
                    return
            case "Share by OTP":
                password = this.otpEntry.get()
                receivingUser = this.userEntryBox.get()
                result = this.vault.shareEncrypted(receivingUser, password, list(this.files))
                if result is False:
                    mb.showerror("User Doesn't Exist", f"The user \"{receivingUser}\" does not exist!")
                    return
        this.clear()

    def shareMethodSwitch(this, value: str) -> None:
        if value == "Share by OTP":
            this.otpEntry.configure(state=ctk.NORMAL)
        else:
            this.otpEntry.configure(state=ctk.DISABLED)