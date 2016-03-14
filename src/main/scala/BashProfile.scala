import scala.io.{BufferedSource, Source}
import scala.collection.mutable.ListBuffer

import java.io._

/**
  * Created by derauk on 2/15/16.
  */

/**
  * The companion object to the BashProfile class
  */
object BashProfile {

  val StartingAliasLine = "### Generated Aliases ###"
  val EndingAliasLine = "### End of Generated Aliases ###"

  /**
    * Create a new profile object based on the
    * file given.
    * @param filename
    * @return
    */
  def create(filename: String = null): BashProfile = {
    val profile = new BashProfile(filename)
    profile.initialize()
    return profile
  }
}

/**
  * An object representing a user's .bash_profile
  * @param filename
  */
class BashProfile(var filename: String = null) {

  private val before = ListBuffer[String]()
  val aliases = ListBuffer[String]()
  private val after = ListBuffer[String]()

  private var readFrom = None: Option[BufferedSource]

  if(filename == null)
    filename = System.getProperty("user.home") + "/.bash_profile"

  /**
    * Initialize the profile object by parsing the information
    * in the users bash profile.
    */
  def initialize(): Unit = {
    readFrom = Some(Source.fromFile(filename))

    var currentList = before

    for (line <- Source.fromFile(filename).getLines) {
      val trimmed = line.stripMargin(' ')

      trimmed match
      {
        case BashProfile.StartingAliasLine =>
          {
            currentList += line
            currentList = aliases
          }
        case BashProfile.EndingAliasLine =>
          {
            currentList = after
            currentList += line
          }
        case default => currentList += line
      }
    }
  }

  /**
    * Write the object as a bash profile
    */
  def write(): Unit = {
    val os = new FileOutputStream(filename, false)

    val sb = StringBuilder.newBuilder

    before.foreach(line => sb.append(s"$line\n"))
    aliases.foreach(line => sb.append(s"$line\n"))
    after.foreach(line => sb.append(s"$line\n"))

    os.write(sb.toString().getBytes())
  }

  def addAlias(alias: String, cmd: String): Unit = {
    // Make sure we're not adding unnecessary single quotes and breaking the command
    val trimmedCommand = cmd.trim.stripPrefix("'").stripSuffix("'").
                                  stripPrefix("\"").stripSuffix("\"")

    if(this.aliasExists(alias)){
      throw new Exception("Alias is already mapped")
    }

    if(!hasAliases) {
      before += "\n"
      before += BashProfile.StartingAliasLine
      after += BashProfile.EndingAliasLine
    }

    aliases += s"alias $alias='$trimmedCommand'"
  }

  def aliasExists(alias: String): Boolean = {
    return aliases.find(x => x.toString().split(' ')(1).startsWith(alias)).isDefined
  }

  /**
    * Check if this profile has any generated aliases
    * @return True if it has aliases generated, false otherwise
    */
  def hasAliases: Boolean = {
    if(!isInitialized)
      {
        throw new Exception("Cannot call hasAliases before initializing the object")
      }

    return aliases.nonEmpty
  }

  /**
    * Check if the profile is initialized
    * @return True if initialized, false otherwise
    */
  def isInitialized: Boolean = {
    return readFrom.nonEmpty
  }
}
