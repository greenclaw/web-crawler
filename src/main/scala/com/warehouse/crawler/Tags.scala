package com.warehouse.crawler

import scala.util.matching.Regex


/*
  Tags is the DTO to load and parse info from response
  implicit strin2Tags convertion is slow because of regex matching
  Thus, to improve performance use plain String instead
 */
case class Tags(title: String, keywords: String, description: String) {
  override def toString: String =
    s"Title:\n$title\nKeywords:\n$keywords\nDescription:\n$description"
}

object Tags {
  val META_DESCR_PATTERN = """<meta\W*content="(.+)"\W*name="description"\W*/?>""".r
  val META_KW_PATTERN = """<meta\W*content="(.+)"\W*name="keywords"\W*/?>""".r
  val TITLE_PATTERN = "<title>.*</title>".r

  private def findRegExp(s: String, pattern: Regex, msg: String): String = {
    pattern.findFirstIn(s).getOrElse(msg)
  }

  private def findTitle: String => String =
    findRegExp(_, Tags.TITLE_PATTERN , "title is not presented")

  private def findKeywords: String => String =
    findRegExp(_, Tags.META_KW_PATTERN , "meta keywords is not presented")

  private def findDescription: String => String =
    findRegExp(_, Tags.META_DESCR_PATTERN , "meta description is not presented")

  implicit def string2Tags(cut: String): Tags =
    Tags(
      title=findTitle(cut),
      keywords=findKeywords(cut),
      description=findDescription(cut)
    )
}

