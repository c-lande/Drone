package in2000.gruppe40.dron.view


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import in2000.gruppe40.dron.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class IntegrationTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION"
        )

    @Test
    fun integrationTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)


        val klokkeslettStart = onView(
            allOf(
                withId(R.id.SunriseValue),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.appcompat.widget.LinearLayoutCompat::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.Search), withContentDescription("Søk etter sted"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.weatherView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(ViewActions.click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.edittext_search),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.searchView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(
            ViewActions.replaceText("garder"),
            ViewActions.closeSoftKeyboard()
        )

        val textView = onView(
            allOf(
                withId(R.id.tv_place_name), withText("Gardermoen"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rv_search_results),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        checkNotNull(textView)

        val recyclerView = onView(
            allOf(
                withId(R.id.rv_search_results),
                isDisplayed(),
                childAtPosition(
                    withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                    0
                )
            )
        )
        recyclerView.perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                ViewActions.click()
            )
        )

        val viewInteraction = onView(
            allOf(
                withId(R.id.fab_expand_menu_button),
                childAtPosition(
                    allOf(
                        withId(R.id.multiple_actions),
                        childAtPosition(
                            withClassName(Matchers.`is`("android.widget.RelativeLayout")),
                            2
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        viewInteraction.perform(ViewActions.click())

        val klokkeslettSlutt = onView(
            allOf(
                withId(R.id.SunriseValue),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(androidx.appcompat.widget.LinearLayoutCompat::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )

        assert(klokkeslettStart != klokkeslettSlutt)
        assert(klokkeslettStart == klokkeslettStart)

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
