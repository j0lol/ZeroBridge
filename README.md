ZeroBridge is a small Discord Bridge for NilLoader. It currently only targets 1.4.7, though support could be added to later or earlier versions in the future.

ZeroBridge has been tested working in:
- Minecraft 1.4.7 + NilLoader
- The modpack Rewind Upsilon

> If you find issues with ZeroBridge in your instance, please file an issue.
>
> Notably, ZeroBridge does not play nice with Forge. Issues have been fixed within tested environments, but if you need ZeroBridge support in a Forge environment, please file an issue.

# Quick start
1. Download [ZeroBridge](https://git.gay/j0/ZeroBridge/releases/tag/0.1.0)
2. Drop ZeroBridge in your `nilmods` folder
3. Write a config at `config/zerobridge.json`. If you do not add a config, ZeroBridge will create a config on first startup. Please terminate the instance if this happens.
4. Launch the game!

## Configuration
This is the default configuration for ZeroBridge. Please note that the template strings `{USER}` and `{MSG}` will be replaced if the template needs it. Bot settings are optional. If you use webhooks, requests will be sent to [Visage](https://visage.surgeplay.com/index.html) to render avatars. Please be mindful of API use.
```json5
{
	// Your Discord token
	"token": "CHANGEME",
	// Guild ID
	"guild": "CHANGEME",
	// Channel ID
	"channel": "CHANGEME",
	// Whether to use webhooks or not
	"webhook": true,
	// Strings to use when displaying events.
	"strings": {
		"server_start": "Server Started",
		"server_stop": "Server Stopped",
		"discord_message_template": "D [{USER}] {MSG}",
		"player_join": "{USER} joined the game",
		"player_leave": "{USER} left the game"
	},
	// Bot settings
	"bot": {
		"avatar_url": null,
		"nickname": null
	}
}
```