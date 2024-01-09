# FileEncrypter

## Introduction
The program serves as an encrypted vault for files of any filetype. Multiple users can create accounts and have their own independent vaults. Besides just encrypting and decrypting files, convenience features such as sharing files with another user are implemented. 

Note: _This is a personal educational project, and should not be used to secure anything serious._

## Installation
1. Download the `.exe` file
2. Create a new folder where you wish to store data, and insert the `.exe` file into it
3. If you have antivirus on, manually "trust" the `.exe` file
4. Install Java, if not already installed
5. Create a system environmental variable called `JAVA_HOME`, set it to the location of your Java installation
6. Run the `.exe` file by double clicking it

## How to Use
Upon startup the user will be presented with an authentication window. A new user can be created, or existing credentials can be modified by clicking the links on the bottom of the window; these will reorient the authentication menu for those purposes. 

After authentication, the user's vault will be unencrypted. Any files in the `unlocked-storage` directory are considered the open vault and can be viewed, modified, removed, or added to as needed. The vault can be controlled as described below in _Vault Menu Options_. The vault will close, meaning all unencrypted files will be purged after re-encryption, upon logging out, quitting the program, or unexpected termination of the program. 

The `encrypted-storage` directory holds encrypted vault contents, and the `USER_FILE.txt` file holds user information and file decryption keys. Neither of these should be interacted with by the user.

## Vault Menu Options

**Flaggable Options:** the behavior of these is impacted by the _saving option_ and _removing option_ dropdown boxes
- `Refresh`- Register vault changes by applying the given options, while staying logged in
- `Logout`- Register vault changes by applying the given options, log out of the vault, and be presented with a new authentication window
- `Quit`- Register vault changes by applying the given options, and terminate the program

**Saving Option:**
- `Add and Save` - register new files, and save any changes made to current files
- `Save Existing Only` - only save any changes made to current files, disregard new files
- `Don't Save` - don't save any changes or additions 

**Removing Options:**
- `Remove from this user` - any files deleted from the open vault will be deregistered for the current user
- `Delete from all users` - any files deleted from the open vault will be deregistered for the current user AND any other users with access
- `Don't Remove` - any files deleted from the open vault will not be deregistered

**Sharing Buttons:**
- `Send Files` - opens the _Send Files_ Menu
- `Inbox (x)` - opens the _Receive Files_ Menu. `x` is the number of files received that are 'unopened'. If no files are 'unopened' the button will be disabled

## Send Files Menu
Use the `Choose Files` and `Clear` button to control which files are being shared; they will be listed in the textbox on the left. Press `Share` to actually send the files. There are 3 ways to send files, which is controlled by the dropdown box:
- `Share by Login` - the sending user will be prompted to authenticate the receiving user, shares immediately
- `Share by username` - the file is encrypted using the receiver's username, less secure. Enter the username in the username entry box
- `Share by OTP` - the file is encrypted with a One Time Password. Enter the receiving username and OTP in their respective entry boxes

Any file can be chosen to be shared, but non-registered files will immediately have a copy encrypted and will be registered with the User File (not the current user).


## Receive Files Menu
Upon opening, all received files encrypted with the current user's username will be unlocked and displayed. To unlock more files using a One Time Password (OTP), enter an OTP in the OTP entry box and click `Try OTP`; any files unlocked by that will be displayed. You may exit from this menu at any time without impacting files that haven't been unlocked yet


## Other Operation Info
- All text combinations are valid for usernames and passwords
- when sharing files, if the receiving vault has a file with the same name as a file being shared, then the shared file will overwrite the file in the receiving vault

## License to Reuse
This source code and any releases may be used for any non-commercial purposes. Please contact me before using the source code, as I'm interested to know where my code travels.
