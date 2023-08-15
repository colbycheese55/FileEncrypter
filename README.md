#PROJECT_NAME

##Background

##Installation

##How to Use
Upon startup the user will be presented with a sign in prompt. To create a new user, enter `NEW_USER` into the username prompt and follow the given prompts to create a new account. If an invalid username or password is given, the user will be prompted to try again until a correct entry is given; `EXIT` may be entered for either to immediantly stop the program. 

After authentication, the user's vault will be unencrypted. Any files in the unlocked-storage directory are considered the open vault and can be viewed, modified, removed, or added to as needed. The vault can be controlled using the commands shown bellow in _Vault Menu Options_. The vault will close, meaning all unencrypted files will be purged after re-encryption, upon logging out, quiting the program, or unexpected termination of the program. 

The `encrypted-storage` directory holds encrypted vault contents, and the `USER_FILE.txt` file holds user information and file decryption keys. Neither of these should be interacted with by the user.

##Vault Menu Options
- `list`- List available files
- `info`- Show user vault info
- `print`- Re-print the vault control instructions to the terminal
- `share`- Share files with another user
- `change`- Change username and / or password

**Flaggable Options:**
- `re`- Register vault changes by applying the given flags, while staying logged in
- `log`- Log out of the vault, and be presented with a new sign in prompt
- `quit`- Log out of the vault, and terminate the program

**Flags:** add flags after the option with a dash, like `quit -ad`
- `s`- Save changes to already registered files, without adding new files
- `a`- Save changes to files and add any new files
- `r`- Remove access to this file from the current user, without deleting it
- `d`- Delete this file entirely, removing access from all users