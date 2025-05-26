# Hide Player

[中文](README.zh.md)

A Minecraft server mod that allows selectively hiding players from various server interfaces.

## Supported Versions

Because I just wrote it for my own use and I'm not sure if anyone else would need it, currently only a few versions are supported. 

You can find supported versions in the [Releases](https://github.com/KoishiMoe/HidePlayer/releases) section. Usually the latest Minecraft version will be supported.

If you need support for other versions, feel free to open an issue or, if possible, a pull request.

## Features

Hide players from:
- System messages (join, leave, death, achievements)
- In-game visibility (player model, chat, tab list, social screen)
- Server status and query responses
- Scoreboard entries

Maybe useful for:
- Service accounts management
- Mini-game development
- Server events
- Staff activities

## Permissions

### Hide Player Permissions
- `hideplayer.hide.*` - Hide player from all interfaces
- `hideplayer.hide.systemmessage` - Hide system messages
- `hideplayer.hide.ingame` - Hide in-game presence and chat
- `hideplayer.hide.statusandquery` - Hide from server status/query
- `hideplayer.hide.scoreboard` - Hide scoreboard entries

### Administrative Permission
- `hideplayer.privilege.seehiddenplayer` - See hidden players (default for ops)

**Note:** Permissions are automatically refreshed on player reconnect to prevent inconsistencies.

## Installation

1. Install a permissions manager
2. Add this mod to your server
3. Configure permissions for target players

## Advanced Configuration

Though generally not necessary, if you face any issues, like mod conflicts, or severe performance drops, you can edit `config/hideplayer.mixin.conf`
and disable mixins selectively. Please note that this may break the mod functionality.

## How it works & Limitations

This mod uses mixins to intercept and modify the behavior of Minecraft's server code. It hides players by preventing their data from being sent to clients. This brings the following benefits:
- No client-side modifications required (works with vanilla clients)
- Impossible for players to bypass the hiding (the client never receives the data)
- Works on some server interfaces that are not easily modifiable (like server status and query)

However, it also has some limitations:
- Some functions this mod injects are called quite frequently, which might cause performance issues (though I haven't noticed any significant impact in my tests)
- It might not work with some both-side mods, if the mod sends player data to the client in a way that this mod cannot intercept. This might cause leaks of hidden player data in some cases, and even crashes the client mod if it tries to access hidden player data. As there're so many mods out there, I can't guarantee compatibility with all of them.
- Players (both the hidden and others) will need to reconnect to the server to reflect any changes in permissions. 
  - This is because, Minecraft does quite a lot of things to tell the client about player joining and leaving. After that, the server only sends updates about player data. If we modify permissions on the fly, the client will just receive some updates, but without initial data, which is confusing for the client and will cause unexpected behaviors.
  - Another reason is that, sometimes permission checking is expensive, and we don't want to do it every time the player data is sent to the client. So we just check permissions when the player joins, and cache the result.

## License

AGPL-3.0-or-later
