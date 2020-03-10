package todo.conf

case class AppConfig(http:HttpConfig, repo:RepoConfig)

case class HttpConfig(port:Int, host:String)

case class RepoConfig(localFile:LocalFileRepoConfig)
case class LocalFileRepoConfig(path:String)