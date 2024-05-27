package sit707_week7;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BodyTemperatureMonitorTest {
	
	private TemperatureSensor temperatureSensor;
    private NotificationSender notificationSender;
    private CloudService cloudService;
    private BodyTemperatureMonitor monitor;
    
    @Before
    public void setUp() {
    	temperatureSensor = Mockito.mock(TemperatureSensor.class);
    	notificationSender = Mockito.mock(NotificationSender.class);
    	cloudService = Mockito.mock(CloudService.class);
    	monitor = new BodyTemperatureMonitor(temperatureSensor, cloudService, notificationSender);
    	
    }

	@Test
	public void testStudentIdentity() {
		String studentId = "221105067";
		Assert.assertNotNull("Student ID is ", studentId);
	}

	@Test
	public void testStudentName() {
		String studentName = "Jiaqi Li";
		Assert.assertNotNull("Student name is ", studentName);
	}
	

	@Test
    public void testReadTemperatureNegative() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(-1.0);
        Assert.assertEquals("temperature is -1",-1.0, monitor.readTemperature(), 0);
    }

    @Test
    public void testReadTemperatureZero() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(0.0);
        Assert.assertEquals("temperature is 0", 0.0, monitor.readTemperature(), 0);
    }

    
    @Test
    public void testReadTemperatureNormal() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(36.8);
        Assert.assertEquals("temperature is 36.8",36.8, monitor.readTemperature(), 0);
    }

    @Test
    public void testReadTemperatureAbnormallyHigh() {
        when(temperatureSensor.readTemperatureValue()).thenReturn(40.0);
        Assert.assertEquals("temperature is 40",40.0, monitor.readTemperature(), 0);
    }


    @Test
    public void testInquireBodyStatusNormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("NORMAL");
        monitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(Customer.class), eq("Thumbs Up!"));
    }

    @Test
    public void testInquireBodyStatusAbnormalNotification() {
        when(cloudService.queryCustomerBodyStatus(any(Customer.class))).thenReturn("ABNORMAL");
        monitor.inquireBodyStatus();
        verify(notificationSender, times(1)).sendEmailNotification(any(FamilyDoctor.class), eq("Emergency!"));
    }
    
    @Test
    public void testReportTemperatureReadingToCloud() {
        TemperatureReading temperatureReading = new TemperatureReading();
        monitor.reportTemperatureReadingToCloud(temperatureReading);
        verify(cloudService, times(1)).sendTemperatureToCloud(temperatureReading);
    }
	
}
