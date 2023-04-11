package gentle.hilt.interop.network.localTestUtil

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockWebServerRule : TestRule {
    val mockWebServer = MockWebServer()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                mockWebServer.start()
                try {
                    base.evaluate()
                } finally {
                    mockWebServer.shutdown()
                }
            }
        }
    }
}