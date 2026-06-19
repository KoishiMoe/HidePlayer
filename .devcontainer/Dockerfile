FROM mcr.microsoft.com/devcontainers/base:debian

ARG DEBIAN_FRONTEND=noninteractive

RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        dbus-user-session \
        fontconfig \
        fonts-dejavu-core \
        git \
        gnupg \
        libasound2 \
        libdrm2 \
        libegl1 \
        libgl1 \
        libgl1-mesa-dri \
        libglx-mesa0 \
        libopenal1 \
        libpulse0 \
        libx11-6 \
        libxcomposite1 \
        libxcursor1 \
        libxdamage1 \
        libxext6 \
        libxfixes3 \
        libxi6 \
        libxinerama1 \
        libxrandr2 \
        libxrender1 \
        libxtst6 \
        libxxf86vm1 \
        mesa-utils \
        mesa-vulkan-drivers \
        procps \
        pulseaudio-utils \
        unzip \
        vulkan-tools \
        xauth \
        xz-utils \
        zip; \
    rm -rf /var/lib/apt/lists/*
