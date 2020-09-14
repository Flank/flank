package flank.scripts.ci.nexttag

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDate

class NextReleaseTagGeneratorTest {

    @Test
    fun `Should increment value for next tag`() {
        mockkStatic(LocalDate::class) {
            every { LocalDate.now() } returns LocalDate.of(2020, 8, 1)
            assertThat(generateNextReleaseTag("v20.08.1")).isEqualTo("v20.08.2")
        }
    }

    @Test
    fun `Should start new tag for new month`() {
        mockkStatic(LocalDate::class) {
            every { LocalDate.now() } returns LocalDate.of(2020, 9, 1)
            assertThat(generateNextReleaseTag("v20.08.1")).isEqualTo("v20.09.0")
        }
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            runCatching {
                mockkStatic(LocalDate::class) {
                    // Mockk probably has a bug because sometimes first call of
                    // every { LocalDate.now() } is failing with strange error:
                    /*
                  every/verify {} block were run several times. Recorded calls count differ between runs
Round 1: class java.time.LocalDate.of(-999999999, 1, 1), class java.time.LocalDate.of(999999999, 12, 31), class java.time.LocalDate.now()
Round 2: class java.time.LocalDate.now()
io.mockk.MockKException: every/verify {} block were run several times. Recorded calls count differ between runs
Round 1: class java.time.LocalDate.of(-999999999, 1, 1), class java.time.LocalDate.of(999999999, 12, 31), class java.time.LocalDate.now()
Round 2: class java.time.LocalDate.now()
	at io.mockk.impl.recording.SignatureMatcherDetector$detect$1.invoke(SignatureMatcherDetector.kt:25)
	at io.mockk.impl.recording.SignatureMatcherDetector.detect(SignatureMatcherDetector.kt:86)
	at io.mockk.impl.recording.states.RecordingState.signMatchers(RecordingState.kt:39)
	at io.mockk.impl.recording.states.RecordingState.round(RecordingState.kt:31)
	at io.mockk.impl.recording.CommonCallRecorder.round(CommonCallRecorder.kt:50)
	at io.mockk.impl.eval.RecordedBlockEvaluator.record(RecordedBlockEvaluator.kt:59)
	at io.mockk.impl.eval.EveryBlockEvaluator.every(EveryBlockEvaluator.kt:30)
	at io.mockk.MockKDsl.internalEvery(API.kt:92)
	at io.mockk.MockKKt.every(MockK.kt:98)
	at flank.scripts.ci.nexttag.NextReleaseTagGeneratorTest.Should start new tag for new month(NextReleaseTagGeneratorTest.kt:23)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.BlockJUnit4ClassRunner$1.evaluate(BlockJUnit4ClassRunner.java:100)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:103)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:63)
	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
	at org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:26)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecutor.runTestClass(JUnitTestClassExecutor.java:110)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecutor.execute(JUnitTestClassExecutor.java:58)
	at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecutor.execute(JUnitTestClassExecutor.java:38)
	at org.gradle.api.internal.tasks.testing.junit.AbstractJUnitTestClassProcessor.processTestClass(AbstractJUnitTestClassProcessor.java:62)
	at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:51)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.internal.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:33)
	at org.gradle.internal.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:94)
	at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
	at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:118)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:36)
	at org.gradle.internal.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:182)
	at org.gradle.internal.remote.internal.hub.MessageHubBackedObjectConnection$DispatchWrapper.dispatch(MessageHubBackedObjectConnection.java:164)
	at org.gradle.internal.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:412)
	at org.gradle.internal.concurrent.ExecutorPolicy$CatchAndRecordFailures.onExecute(ExecutorPolicy.java:64)
	at org.gradle.internal.concurrent.ManagedExecutorImpl$1.run(ManagedExecutorImpl.java:48)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at org.gradle.internal.concurrent.ThreadFactoryImpl$ManagedThreadRunnable.run(ThreadFactoryImpl.java:56)
	at java.lang.Thread.run(Thread.java:748)
                    */
                    // simple workaround is to call `every` before test class
                    every { LocalDate.now() } returns LocalDate.of(1, 1, 1)
                }
            }
        }
    }
}
