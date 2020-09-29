package grails.plugins.quartz

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

/**
 * Unit tests for TriggerDescriptor
 *
 * @author Vitalii Samolovskikh aka Kefir
 */
class TriggerDescriptorTests {
    private Scheduler scheduler
    private JobDetail job
    private Trigger trigger

    @Before
    void prepare() {
        scheduler = StdSchedulerFactory.getDefaultScheduler()

        scheduler.start()

        job = JobBuilder.newJob(TestQuartzJob).withIdentity(new JobKey("job", "group")).build()
        trigger = TriggerBuilder.newTrigger()
                .withIdentity(new TriggerKey("trigger", "group"))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(2).repeatForever())
                .startNow()
                .build()

        scheduler.scheduleJob(job, trigger)
    }

    @After
    void dispose() {
        scheduler.shutdown()
    }

    @Test
    void testBuild() {
        TriggerDescriptor descriptor =
                TriggerDescriptor.build(JobDescriptor.build(job, scheduler), trigger, scheduler)
        assert descriptor.name == 'trigger'
        assert descriptor.group == 'group'
        assert descriptor.state == Trigger.TriggerState.NORMAL
    }
}
