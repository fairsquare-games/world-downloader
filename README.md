# WorldDownloader

This repository contains a Spigot plugin that can be used to download worlds from a Minecraft
server. These worlds will be uploaded to the
[world-downloader-server](https://github.com/fairsquare-games/world-downloader-server), where they
can then be downloaded from using a dedicated URL.

## Quick Start

1. Download and set up the
[world-downloader-server](https://github.com/fairsquare-games/world-downloader-server).
2. Download the latest version of the plugin from the
[releases](https://github.com/fairsquare-games/world-downloader/releases) section.
3. Stop your Spigot server.
4. Drag and drop the downloaded .jar file into the plugins directory of your Spigot server.
5. Start the server again.
6. Configure the URLs to be that of your own
[world-downloader-server](https://github.com/fairsquare-games/world-downloader-server).
7. Restart the server (a reload command still needs to be added, so restarting is necessary at the
moment unfortunately)

## Usage

There is only one command:

- `/downloadworld`. (Permission: `worlddownloader.download`) This will download the world that the
user is currently standing in.

Other than this command, **you must configure the world-downloader-server's URL correctly in the
config.yml**. Since Fair Square Games doesn't offer a public URL (as of yet), you can download the
repository that contains this server yourself 
[here](https://github.com/fairsquare-games/world-downloader-server). Instructions on how to start
and configure this service can be found in the README of that repository.

## Disclaimer

This plugin is designed for Fair Square Games and is tuned for its use cases. Although this plugin
can be used on other servers as well, we take no responsibility for any damage caused by using
this plugin (improperly).
