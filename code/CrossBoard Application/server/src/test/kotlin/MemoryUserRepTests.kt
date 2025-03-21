import httpModel.UserProfileInfo
import model.Email
import model.Password
import model.User
import model.Username
import repository.memoryRepositories.MemoryUserRep
import kotlin.test.*

class MemoryUserRepTests {
    @Test fun getUserProfileByIdWithSuccess() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileById(1U)
        val userResult = UserProfileInfo(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"))

        assertEquals(userInfo, userResult)
        assertEquals(userInfo?.id, userResult.id)
        assertEquals(userInfo?.username, userResult.username)
        assertEquals(userInfo?.email, userResult.email)
    }

    @Test fun getUserProfileByIdWithFailure() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileById(4U)
        assertNull(userInfo)
    }

    @Test fun getUserProfileByEmailWithSuccess() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileByEmail(Email("A48926@alunos.isel.pt"))
        val userResult = UserProfileInfo(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"))

        assertEquals(userInfo, userResult)
        assertEquals(userInfo?.id, userResult.id)
        assertEquals(userInfo?.username, userResult.username)
        assertEquals(userInfo?.email, userResult.email)
    }

    @Test fun getUserProfileByEmailWithFailure() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileByEmail(Email("A48927@alunos.isel.pt"))

        assertNull(userInfo)
    }

    @Test fun getUserProfileByNameWithSuccess() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileByName(Username("Rúben Louro"))
        val userResult = UserProfileInfo(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"))

        assertEquals(userInfo, userResult)
        assertEquals(userInfo?.id, userResult.id)
        assertEquals(userInfo?.username, userResult.username)
        assertEquals(userInfo?.email, userResult.email)
    }

    @Test fun getUserProfileByNameWithFailure() {
        val userRep = MemoryUserRep()
        val userInfo = userRep.getUserProfileByName(Username("Rúben Louros"))
        assertNull(userInfo)
    }

    @Test fun deleteUserWithSuccess() {
        val userRep = MemoryUserRep()
        val userResult = userRep.deleteUser(1U)

        assertTrue{userResult}

        val userSearch = userRep.getUserProfileById(1U)

        assertNull(userSearch)
    }

    @Test fun deleteUserWithFailure() {
        val userRep = MemoryUserRep()
        val userResult = userRep.deleteUser(4U)

        assertFalse{userResult}
    }

    @Test fun updateUserWithSuccessWithNewName() {
        val userRep = MemoryUserRep()
        val newInfo = userRep.updateUser(1U, Username("Rúben Louros"), null, null)

        val userResult = UserProfileInfo(1U, Username("Rúben Louros"), Email("A48926@alunos.isel.pt"))

        assertEquals(newInfo, userResult)
        assertEquals(newInfo.id, userResult.id)
        assertEquals(newInfo.username, userResult.username)
        assertEquals(newInfo.email, userResult.email)
    }

    @Test fun updateUserWithSuccessWithNewEmail() {
        val userRep = MemoryUserRep()
        val newInfo = userRep.updateUser(1U, null, Email("A48927@alunos.isel.pt"), null)

        val userResult = UserProfileInfo(1U, Username("Rúben Louro"), Email("A48927@alunos.isel.pt"))

        assertEquals(newInfo, userResult)
        assertEquals(newInfo.id, userResult.id)
        assertEquals(newInfo.username, userResult.username)
        assertEquals(newInfo.email, userResult.email)
    }

    @Test fun updateUserWithSuccessWithNewPassword() {
        val userRep = MemoryUserRep()
        val newInfo = userRep.updateUser(1U, null, null, Password("Aa123456!"))

        val userResult = UserProfileInfo(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"))

        assertEquals(newInfo, userResult)
        assertEquals(newInfo.id, userResult.id)
        assertEquals(newInfo.username, userResult.username)
        assertEquals(newInfo.email, userResult.email)
    }

    @Test fun getUserFullDetailsWithSuccess() {
        val userRep = MemoryUserRep()
        val userResult = userRep.getUserFullDetails(1U)
        val userSearched = User(1U, Username("Rúben Louro"), Email("A48926@alunos.isel.pt"), Password("Aa12345!"))

        assertEquals(userResult, userSearched)
        assertEquals(userResult?.id, userSearched.id)
        assertEquals(userResult?.username, userSearched.username)
        assertEquals(userResult?.email, userSearched.email)
        assertEquals(userResult?.password, userSearched.password)
    }

    @Test fun getUserFullDetailsWithFailure() {
        val userRep = MemoryUserRep()
        val userResult = userRep.getUserFullDetails(4U)
        assertNull(userResult)
    }

    @Test fun addUserWithSuccess() {
        val userRep = MemoryUserRep()
        val newUser = userRep.addUser(Username("Ambrosio Vitorino"), Email("ambrosio@hotmail.com"), Password("Aa12345!"))

        val userAddedInfo = UserProfileInfo(4U, Username("Ambrosio Vitorino"), Email("ambrosio@hotmail.com"))

        assertEquals(newUser, userAddedInfo)
        assertEquals(newUser.id, userAddedInfo.id)
        assertEquals(newUser.username, userAddedInfo.username)
        assertEquals(newUser.email, userAddedInfo.email)
    }
}