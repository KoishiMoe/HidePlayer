# Hide Player

A Minecraft server mod that allows selectively hiding players from various server interfaces.

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

## License

AGPL-3.0-or-later