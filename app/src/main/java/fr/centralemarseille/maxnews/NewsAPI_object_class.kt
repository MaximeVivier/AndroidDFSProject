package fr.centralemarseille.maxnews

data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val urlToImage: String,
    val description: String,
    val url: String,
    val publishedAt: String,
    val content: String,
)

data class SourcesObjectFromAPINews(
    val status: String,
    val sources: Array<Source>
)

data class ArticlesObjectFromAPINews(
    val status: String,
    val articles: Array<Article>
)

data class Source(
    val id: String,
    val name: String,
    val description: String,
    val url: String,
    val category: String,
    val language: String,
    val country: String,
)