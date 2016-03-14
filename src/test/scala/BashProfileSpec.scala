/**
  * Created by derauk on 2/19/16.
  */

/**
  * Test suite for the BashProfile class
  */
class BashProfileSpec extends UnitSpec {
  "A BashProfile" should "use the filename parameter if passed to it" in {
    val fakeProfile = "/home/ironman/profiles/5"
    val profile = new BashProfile(fakeProfile)
    assert(profile.filename === fakeProfile)
  }
}
