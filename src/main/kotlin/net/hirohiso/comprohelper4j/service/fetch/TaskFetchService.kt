package net.hirohiso.comprohelper4j.service.fetch

import net.hirohiso.comprohelper4j.type.SampleIO
import org.jsoup.Connection
import org.jsoup.Jsoup

class TaskFetchService {
    fun fetch(url: String): FetchResult {
        println(url)
        val doc = try {
            Jsoup.connect(url).method(Connection.Method.POST).get()
                ?:return FetchResult.Error(FetchError.ConnectFailed)
        }catch (e:Exception){
            return FetchResult.Error(FetchError.ConnectFailed)
        }

        println(doc)

        val elements = doc.select("span.lang-ja>div.io-style~div.part>section")
            ?: return FetchResult.Error(FetchError.UnsupportedContestPage)
        val list = elements.stream().map{e -> e.select("pre:first-of-type")}.toList()
        //[id^=pre-sample]
        list.forEach{e -> println(e)}

        return FetchResult.OK(
            list.chunked(2).map { pair ->
                val input = pair[0].text()
                val output = pair[1].text()
                val sampleIO = SampleIO(input, output)
                sampleIO
            }
        )
    }
}