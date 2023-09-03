# FileEncrypter

## Introduction
The program serves as an encrypted vault for files of any filetype. Multiple users can create accounts and have their own independent vaults. Besides just, encrypting and decrypting files, convenience features such as sharing files with another user are implemented. 

Note: _This is a personal educational project, and should not be used to secure anything serious._

## Installation
1. Download the `.exe` file
2. Create a new folder where you wish to store data, and insert the `.exe` file into it
3. If you have antivirus on, manually "trust" the `.exe` file
4. Install Java, if not already installed
5. Run the `.exe` file by double clicking it

## How to Use
Upon startup the user will be presented with a sign in prompt. To create a new user, enter `NEW_USER` into the terminal and follow the given prompts to create a new account. If an invalid username or password is given, the user will be prompted to try again until a correct entry is given; `EXIT` may be entered during sign in to quit the program. 

After authentication, the user's vault will be unencrypted. Any files in the `unlocked-storage` directory are considered the open vault and can be viewed, modified, removed, or added to as needed. The vault can be controlled using the commands shown below in _Vault Menu Options_. The vault will close, meaning all unencrypted files will be purged after re-encryption, upon logging out, quitting the program, or unexpected termination of the program. 

The `encrypted-storage` directory holds encrypted vault contents, and the `USER_FILE.txt` file holds user information and file decryption keys. Neither of these should be interacted with by the user.

## Vault Menu Options
- `list`- List available files
- `info`- Show user vault info
- `print`- Re-print the vault control instructions to the terminal
- `share`- Share files with another user
- `change`- Change username and / or password

**Flaggable Options:** these options can be followed by a flag(s)
- `re`- Register vault changes by applying the given flags, while staying logged in
- `log`- Log out of the vault, and be presented with a new sign in prompt; without flags the vault remains unchanged
- `quit`- Log out of the vault, and terminate the program; without flags the vault remains unchanged

**Flags:** add flags after the option with a dash, like `quit -ad`
- `s`- Save changes to already registered files only, without adding new files
- `a`- Save changes to files and add any new files
- `r`- Remove access to this file from the current user, without deleting it. If no other users have access, the file will be deleted
- `d`- Delete this file entirely, removing access from all users

## Other Operation Info
- All text combinations are valid for usernames (except `NEW_USER` and `EXIT`) and passwords
- the `share` command will save and add all files (`-a`) immediately upon invocation
- when sharing files, if the receiving vault has a file with the same name as a file being shared, then the shared file will overwrite the file in the receiving vault

## License to Reuse
This source code and any releases may be used for any non-commercial purposes. Please contact me before using the source code, as I'm interested to know where my code travels.