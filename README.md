SecretWord
==========

When a player joins the server they are asked to type a 'secret' word in the chat. All of their chat messages are suppressed
until they provide the word. If they dont provide the word quick enough they will be kicked from the server. After being kicked
a few times they will take a 24h ban. After entering the correct word the player will not need to reenter the word again
until the server is restarted/reloaded and they rejoin.

# Configuration

```yaml
secret word: Hi
seconds before kick: 30
kicks before ban: 3
```

## `secret word`

This is the word they must type exactly (case-insensitive) for the supression to be removed.

## `seconds before kick`

This is how many seconds they have to enter the word before they are automatically kicked

## `kicks before ban`

How many kicks are allowed before it is a ban. 3 means on the 3rd failure ban instead. Use 0 if you want there to be no
bans and always kicks

# Commands

`/secretword <word>`

Sets the secret word and writes it to the config file. All players joining must use this word now, useful to change the
word without reloading the server.

# Permissions

## `uhc.secretword.command`

Default op. Allows use of the /secretword command

## `uhc.secretword.immune`

Default op. Makes the player never require to type a word on join