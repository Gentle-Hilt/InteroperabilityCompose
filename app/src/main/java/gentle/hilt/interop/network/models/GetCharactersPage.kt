package gentle.hilt.interop.network.models

data class GetCharactersPage(
    val info: PageInfo = PageInfo(),
    val results: List<CharacterDetails> = emptyList()
)

data class PageInfo(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String? = null,
    val prev: String? = null
)
