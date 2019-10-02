import interop.*
import kotlinx.cinterop.*
import kotlin.system.exitProcess
import platform.posix.size_t

fun CPointer<ByteVar>.toKString(length: Int): String {
    val bytes = this.readBytes(length)
    return bytes.decodeToString()
}

fun main()
{
  val curl = curl_easy_init()
  if(curl == null)
    exitProcess(-1)


  curl_easy_setopt(curl, CURLOPT_USERAGENT, "libcurl-agent/1.0");
  curl_easy_setopt(curl, CURLOPT_URL, "https://curl.haxx.se/libcurl/c/getinmemory.html")
  curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L)

  curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, staticCFunction {
        contents : CPointer<ByteVar>?,
        size : size_t,
        nmemb : size_t,
        userp : COpaquePointer? ->

        val actualSize = size * nmemb
        val str = contents?.toKString(actualSize.toInt())

        actualSize
      } )

//  curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);


  val res = curl_easy_perform(curl)

  if(res != CURLE_OK)
      println("curl_easy_perform() failed: ${curl_easy_strerror(res)?.toKString()}")

  curl_easy_cleanup(curl)

}
