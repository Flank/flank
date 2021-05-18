package ftl.client.google

import com.google.testing.model.ClientInfoDetail

internal fun Map<String, String>.toClientInfoDetailList() = map { (key, value) ->
    ClientInfoDetail()
        .setKey(key)
        .setValue(value)
}
