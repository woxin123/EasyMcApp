package top.mcwebsite.novel.data.local.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object SearchHistoriesSerializer : Serializer<SearchHistories> {
    override val defaultValue: SearchHistories = SearchHistories.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SearchHistories {
        try {
            return SearchHistories.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Can not read proto", exception)
        }
    }

    override suspend fun writeTo(t: SearchHistories, output: OutputStream) {
        t.writeTo(output)
    }
}