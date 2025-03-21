# Hide Player

[中文](README.zh.md)

A Minecraft server mod that allows selectively hiding players from various server interfaces.

## Supported Versions

Currently only 1.21.4+ is supported, because I just wrote it for my own use and I'm not sure if anyone else would need it.
If you need support for other versions, feel free to open an issue or a pull request.

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

## License

AGPL-3.0-or-later