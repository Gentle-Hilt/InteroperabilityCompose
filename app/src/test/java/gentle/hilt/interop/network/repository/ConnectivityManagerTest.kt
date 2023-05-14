package gentle.hilt.interop.network.repository

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import gentle.hilt.interop.network.localTestUtil.TestCoroutineRule
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class ConnectivityManagerTest {
    @get:Rule
    val rule = TestCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var connectivityManager: ConnectivityManager

    @MockK
    lateinit var network: Network

    private val validNetworks: MutableSet<Network> = HashSet()

    private lateinit var capabilities: NetworkCapabilities
    @Before
    fun setup() {
        MockKAnnotations.init(this)

        // Creating available network
        capabilities = mockNetworkCapabilities(hasTransportCellular = true, hasTransportWifi = true)
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        validNetworks.add(network)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // Im simulating the behavior of getNetworkCapabilities in the ConnectivityManager
    // to test connected() and indirectly networkCallbacks that way
    // in the future i can easily add new callbacks and test it right away
    @Test
    fun `connected() return true when network is available`() = runBlocking {
        // Just assertion if we really created network
        assertThat(connected()).isTrue()
    }

    @Test
    fun `connected() return false when network is unavailable`() = runBlocking {
        // Behavior of connectivity manager when connection becomes unavailable
        every { connectivityManager.getNetworkCapabilities(network) } returns null
        assertThat(connected()).isFalse()
    }

    @Test
    fun `connected() should return false after connection lost`() = runBlocking {
        //  Behavior of connectivity manager when connection becomes lost
        capabilities = mockNetworkCapabilities(hasTransportCellular = false, hasTransportWifi = false)
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        assertThat(connected()).isFalse()
    }

    private fun mockNetworkCapabilities(
        hasTransportCellular: Boolean = false,
        hasTransportWifi: Boolean = false
    ): NetworkCapabilities {
        val capabilities = mockk<NetworkCapabilities>()

        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns hasTransportCellular
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns hasTransportWifi

        return capabilities
    }

    private fun connected(): Boolean {
        return validNetworks.any { network ->
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val hasCellularTransport = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
            val hasWifiTransport = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            hasCellularTransport || hasWifiTransport
        }
    }
}