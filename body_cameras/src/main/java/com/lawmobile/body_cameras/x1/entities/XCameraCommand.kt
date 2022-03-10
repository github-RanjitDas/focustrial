package com.lawmobile.body_cameras.x1.entities

import com.lawmobile.body_cameras.entities.BWCConnectionParams

class XCameraCommand(builder: Builder) {
    private var token: Int = 0
    private var msg_id: Int = 0
    private var param: String? = null
    private var offset: Int? = null
    private var fetch_size: Int? = null
    private var size: Int? = null
    private var md5sum: String? = null
    private var type: String? = null

    constructor(block: Builder.() -> Unit) : this(Builder().apply(block))

    init {
        token = BWCConnectionParams.sessionToken
        msg_id = builder.valueCommand
        param = builder.parameter
        offset = builder.offsetSize
        fetch_size = builder.fetchSize
        md5sum = builder.hashFile
        type = builder.commandType
        size = builder.size
    }

    class Builder {
        var valueCommand: Int = 0
            private set
        var parameter: String? = null
            private set
        var offsetSize: Int? = null
            private set
        var fetchSize: Int? = null
            private set
        var hashFile: String? = null
            private set
        var commandType: String? = null
            private set
        var size: Int? = null
            private set

        fun addMsgId(msgId: Int): Builder {
            this.valueCommand = msgId
            return this
        }

        fun addParam(parameter: String): Builder {
            this.parameter = parameter
            return this
        }

        fun addOffset(offset: Int): Builder {
            this.offsetSize = offset
            return this
        }

        fun addFetchSize(fetchSize: Int): Builder {
            this.fetchSize = fetchSize
            return this
        }

        fun addMD5Sum(hashFile: String): Builder {
            this.hashFile = hashFile
            return this
        }

        fun addType(commandType: String): Builder {
            this.commandType = commandType
            return this
        }

        fun addSize(size: Int): Builder {
            this.size = size
            return this
        }

        fun build(): XCameraCommand {
            return XCameraCommand(this)
        }
    }
}
