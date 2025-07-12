package modelTests

import androidx.compose.runtime.TestOnly
import com.crossBoard.domain.*
import com.crossBoard.model.AdminState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class AdminStateTests {

    @Test fun adminStateTest() {
        val result = AdminState()

        assertEquals("", result.searchQuery)
        assertEquals(emptyList<UserInfo>(), result.searchResults)
        assertFalse(result.isSearching)
        assertNull(result.searchError)
        assertNull(result.selectedUser)
        assertFalse(result.isModifyingUser)
        assertNull(result.modifyUserError)
        assertNull(result.modifyUserSuccess)

        val userInfo = UserInfo(1, Token("test"), Username("test1"), Email("test1@test.com"),"NORMAL")

        val result2 = AdminState(
            searchQuery = "testQuery",
            searchResults = listOf(userInfo),
            isSearching = true,
            searchError = "testError",
            selectedUser = userInfo,
            isModifyingUser = true,
            modifyUserError = "modifyError",
            modifyUserSuccess = "modifySuccess"
        )

        assertEquals("testQuery", result2.searchQuery)
        assertEquals(listOf(userInfo), result2.searchResults)
        assertEquals(true, result2.isSearching)
        assertEquals("testError", result2.searchError)
        assertEquals(userInfo, result2.selectedUser)
        assertEquals(true, result2.isModifyingUser)
        assertEquals("modifyError", result2.modifyUserError)
        assertEquals("modifySuccess", result2.modifyUserSuccess)
    }

}