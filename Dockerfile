FROM openjdk:8

# Set up environment variables
ENV ANDROID_HOME="/home/user/android-sdk-linux" \
    SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-6200805_latest.zip" \
    GRADLE_URL="https://services.gradle.org/distributions/gradle-5.6.4-all.zip"

ENV LC_ALL=en_US.UTF-8 \
    LANG=en_US.UTF-8

# Install Git and dependencies
RUN dpkg --add-architecture i386 \
    && apt-get update \
    && apt-get install -y file git curl zip libncurses5:i386 libstdc++6:i386 zlib1g:i386 libc6-dev g++ \
    && apt-get update

# Install Fastlane
RUN apt-get install build-essential -y
RUN apt-get install -y ruby ruby-dev
RUN gem install fastlane
RUN gem install google-api-client -v 0.29.1

# Clean Image
RUN apt-get clean \
    && rm -rf /var/lib/apt/lists /var/cache/apt

# Create a non-root user
RUN useradd -m user
USER user
WORKDIR /home/user
# Download Android SDK
RUN mkdir "$ANDROID_HOME" .android \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip $SDK_URL \
    && unzip sdk.zip \
    && rm sdk.zip \
    && yes Y | $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} --install "build-tools;28.0.3" "platforms;android-29" "platform-tools" \
    && yes | $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} --licenses
# Install Gradle
RUN wget $GRADLE_URL -O gradle.zip \
    && unzip gradle.zip \
    && mv gradle-5.6.4 gradle \
    && rm gradle.zip \
    && mkdir .gradle
ENV PATH="/home/user/gradle/bin:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${PATH}"