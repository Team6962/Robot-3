package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.ColorSensorV3;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.constraint.CentripetalAccelerationConstraint;
import edu.wpi.first.wpilibj.util.Color;
import jdk.swing.interop.DropTargetContextWrapper;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;

public class Robot extends TimedRobot {

  //Joysticks
    Joystick joystick0;
    Joystick joystick1;

  //Motor controllers

    //Sparks
      Spark rbank;
      Spark lbank;
      Spark elevator;
      Spark transfer;
      Spark outtake;
      Spark winch;

    //Victors
      VictorSP drawer;
      VictorSPX intake;
      VictorSPX ctrlpnl;

  //Drivetrain
    DifferentialDrive myDrive;

  //User Input Values;
    double joystickLValue;
    double joystickRValue;
    static final double limitTurnSpeed = 0.75;
    long startDelay = 0;
    boolean pullIn;
    ArrayList povMode;
    long start;

  //Color Sensor
    I2C.Port i2cPort = I2C.Port.kOnboard;
    ColorSensorV3 colorSensor = new ColorSensorV3( i2cPort );
    int colorCount;
    String lastColor;

  //Limit Switches
    //Note that the unclicked state of the limit switch with .get() returns true,
    //and the clicked state returns false.
    DigitalInput drawerIn;
    DigitalInput drawerOut;
    DigitalInput startBelt;
    DigitalInput stopBelt;

  //Endcoders
    Encoder encoder1;
    Encoder encoder2;

  //Testing values
    int counter = 0;

  // Camera
    UsbCamera camera;
    double[] targetAngleValue = new double[ 1 ];
    double[] ballAngleValue = new double[ 1 ]; 

  public static final int WINDOW_WIDTH = 1280;
  public static final int WINDOW_HEIGHT = 720;

  private Object imgLock = new Object();
  Mat cameraMatrix;
  Mat distCoeffs;

  @Override
  public void robotInit() {

    System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
    cameraMatrix = new Mat();
    distCoeffs = new Mat();
    //Init Joysticks
    joystick0 = new Joystick( 0 );
    joystick1 = new Joystick( 1 );
    //Init Drivetrain
    rbank = new Spark( 0 );
    lbank = new Spark( 1 );
    myDrive = new DifferentialDrive( lbank, rbank );
    //Other Motors
    transfer = new Spark( 2 );
    elevator = new Spark( 3 );
    outtake = new Spark( 4 );
    winch = new Spark( 5 );
    drawer = new VictorSP( 9 );
    intake = new VictorSPX( 10 );
    ctrlpnl = new VictorSPX( 20 );
    //Color Sensor
    lastColor = getColor();
    //Limit Switches
    drawerIn = new DigitalInput( 9 );
    drawerOut = new DigitalInput( 8 );
    startBelt = new DigitalInput( 7 );
    stopBelt = new DigitalInput( 6 );
    pullIn = false;
    //Encoders
    encoder1 = new Encoder( 0, 1 ); //Left Encoder
    encoder2 = new Encoder( 2, 3 ); //Right Encoder
    //Driver Assist
    povMode = new ArrayList<Double>();

    new Thread(() -> {
      FindTarget.setup();
      FindBall.readCalibrationData( "calib-logitech.mov-720-30-calib.txt", cameraMatrix, distCoeffs );

      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
      camera.setResolution( WINDOW_WIDTH, WINDOW_HEIGHT );
      CvSink cvSink = CameraServer.getInstance().getVideo();
      //CvSource outputStream = CameraServer.getInstance().putVideo( "Blur", 640, 480 );

      Mat source = new Mat();
      //Mat output = new Mat();

      while( !Thread.interrupted() ) {
        if ( cvSink.grabFrame( source ) == 0 ) {
          continue;
        }
        synchronized( imgLock ) {
          ballAngleValue[ 0 ] = FindBall.getBallValue( source, WINDOW_WIDTH, WINDOW_HEIGHT, cameraMatrix, distCoeffs );
        }
        // SmartDashboard.putNumber( "ballAngleValue", ballAngleValue[ 0 ] );
      }
    }).start();
  
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
    start = System.currentTimeMillis();
  }

  @Override
  public void autonomousPeriodic() {
    long now = System.currentTimeMillis();
    if((encoder1.getDistance() >= -200 || encoder1.getDistance() >= -200)&&now-start <8000){
      myDrive.tankDrive(-0.7, -0.7);
    }else if(now-start <8000) {
      outtake.set(0.65);
      transfer.set(-0.7);
      myDrive.tankDrive(0, 0);
    }else if(now-start < 9500){
      myDrive.tankDrive(0.7, 0.7);
    }else{
      myDrive.tankDrive(0, 0);
    }
    if( drawerOut.get() ) drawer.set( -0.7 ); //If the switch indicating that the drawer isn't all the way out isn't clicked, then make the drawer move out.
    else drawer.set( 0 ); //Else, stop moving.
    

  }

  public String getColor() {

        String colorString;
        double range = 0.05;
        Color detectedColor = colorSensor.getColor(); //Asks the sensor for the colour, following section interprets the return.
        if( ( detectedColor.red <= 0.1551 + range && detectedColor.red >= 0.1551 - range ) && ( detectedColor.green <= 0.4444 + range && detectedColor.green >= 0.4444 - range ) && ( detectedColor.blue <= 0.4001 + range && detectedColor.blue >= 0.3901 - range ) ) {
          colorString = "Red";
        } else if( ( detectedColor.red <= 0.5173 + range && detectedColor.red >= 0.4073 - range ) && ( detectedColor.green <= 0.3488 + range && detectedColor.green >= 0.3488 - range ) && ( detectedColor.blue <= 0.134 + range && detectedColor.blue >= 0.134 - range ) ) {
          colorString = "Blue";
        } else if( ( detectedColor.red <= 0.1899 + range && detectedColor.red >= 0.1899 - range ) && ( detectedColor.green <= 0.5598 + range && detectedColor.green >= 0.5598 - range ) && ( detectedColor.blue <= 0.2501 + range && detectedColor.blue >= 0.2501 - range ) ) {
          colorString = "Yellow";
        } else if( ( detectedColor.red <= 0.3271 + range && detectedColor.red >= 0.3271 - range ) && ( detectedColor.green <= 0.5385 + range && detectedColor.green >= 0.5385 - range ) && ( detectedColor.blue <= 0.134 + range && detectedColor.blue >= 0.134 - range ) ) {
          colorString = "Green";
        } else {
          colorString = "Unknown";
        }
        //Returns the detected and interpreted colour.
        return colorString;

  }

  public int mode(ArrayList<Integer> a){
    int center = 0;
    int top = 0;
    int topRight = 0;
    int right = 0;
    int bottomRight = 0;
    int bottom = 0;
    int bottomLeft = 0;
    int left = 0;
    int topLeft = 0;
    for(int i = 0; i < a.size(); i++){
      if((double)(a.get(i)) == -1){
        center++;
      }else if((double)(a.get(i)) == 0){
        top++;
      }else if((double)(a.get(i)) == 45){
        topRight++;
      }else if((double)(a.get(i)) == 90){
        right++;
      }else if((double)(a.get(i)) == 135){
        bottomRight++;
      }else if((double)(a.get(i)) == 180){
        bottom++;
      }else if((double)(a.get(i)) == 225){
        bottomLeft++;
      }else if((double)(a.get(i)) == 270){
        left++;
      }else if((double)(a.get(i)) == 315){
        topLeft++;
      }
    }
    int maxUse = Math.max(center,Math.max(top,Math.max(topRight,Math.max(right,Math.max(bottomRight,Math.max(bottom,Math.max(bottomLeft,Math.max(left,topLeft))))))));
    
    if(maxUse == center){
      return -1;
    }else if(maxUse == top){
      return 0;
    }else if(maxUse == topRight){
      return 45;
    }else if(maxUse == right){
      return 90;
    }else if(maxUse == bottomRight){
      return 135;
    }else if(maxUse == bottom){
      return 180;
    }else if(maxUse == bottomLeft){
      return 235;
    }else if(maxUse == left){
      return 270;
    }else if(maxUse == topLeft){
      return 315;
    }
    return -1;
  }

  @Override
  public void teleopPeriodic() {

    synchronized( imgLock ) {
      SmartDashboard.putNumber( "ballAngleValue", ballAngleValue[ 0 ] );
    }

    //Color Sensor
      Color detectedColor = colorSensor.getColor();
      String colorString = getColor();
      DriverStation ds = DriverStation.getInstance();

      if( !lastColor.equals( colorString ) && ( colorString.equals( "Red" ) || colorString.equals( "Blue" ) || colorString.equals( "Yellow" ) || colorString.equals( "Green" ) ) ) {

        colorCount++;
        lastColor = colorString;

      }
      
      //Essentially put the specified values on the smart dashboard.
      SmartDashboard.putNumber( "Red", detectedColor.red );
      SmartDashboard.putNumber( "Green", detectedColor.green );
      SmartDashboard.putNumber( "Blue", detectedColor.blue );
      SmartDashboard.putString( "Detected Color", colorString );
      SmartDashboard.putNumber( "colorCount", colorCount );
      SmartDashboard.putString( "End Color", DriverStation.getInstance().getGameSpecificMessage() );

    //Control Panel Manipulator
    if( joystick1.getRawButton( 11 ) ) colorCount = 0;
    if( colorCount <= 32 && joystick1.getRawButton( 7 ) ) {
       ctrlpnl.set( ControlMode.PercentOutput, 0.3 );
    } else if( joystick1.getRawButton( 8 ) && !colorString.equals( ds.getGameSpecificMessage() ) ) {
       ctrlpnl.set( ControlMode.PercentOutput, 0.3 );
    } else {
       ctrlpnl.set( ControlMode.PercentOutput, 0 );
    }
    //Hook
      if( joystick1.getRawButton( 1 ) ) elevator.set( 0.3 );
      else if( joystick1.getRawButton( 2 ) ) elevator.set( -0.3 );
      else elevator.set( 0 );
      winch.set( joystick1.getRawAxis( 1 ) );

    //Gun
      //Transfer
        SmartDashboard.putBoolean( "canTransfer?", startBelt.get() );
        if( !startBelt.get() ) {
          transfer.set( -1 );
          startDelay = System.currentTimeMillis();
        } else if( !stopBelt.get() ) {
          transfer.set( 0 );
        } else {
          if(pullIn){
            intake.set(ControlMode.PercentOutput, 0);
          }else{
            intake.set( ControlMode.PercentOutput, -0.75);
          }
        }

        if( !( transfer.get() == 0 ) ) intake.set( ControlMode.PercentOutput, -0.5 );
        else{ 
          if(pullIn){
            intake.set(ControlMode.PercentOutput, 0);
          }else{
            intake.set( ControlMode.PercentOutput, -0.75);
          }
        }

      //Outtake
        SmartDashboard.putNumber("Wheel Speed", (-joystick0.getRawAxis(3)+1)/2);
        if( joystick0.getRawButton( 1 ) ) {
          intake.set( ControlMode.PercentOutput, 0 );
          transfer.set( -0.8 );
        } else if( joystick0.getRawButton( 2 ) ) {
          transfer.set( 0 );
          outtake.set( 0 );
        }
        outtake.set( -( joystick0.getRawAxis( 3 ) - 1 ) / 2 );
      //Drawer +ve = in && -ve = out
        if( joystick0.getRawButton( 7 ) ) pullIn = false;
        if( joystick0.getRawButton( 8 ) ) pullIn = true;
        if( pullIn && drawerIn.get() ) drawer.set( 0.7 );
        else if( !pullIn && drawerOut.get() ) drawer.set( -0.7 );
        else drawer.set( 0 );

    //Drive Train
      //Encoders
        SmartDashboard.putNumber( "Left Encoder", encoder1.getDistance() );
        SmartDashboard.putNumber( "Right Encoder", encoder2.getDistance() );
        if( joystick0.getRawButton( 9 ) ) {
          encoder1.reset();
          encoder2.reset();
        }

      //Drive
        joystickLValue = ( -joystick0.getRawAxis( 1 ) + ( joystick0.getRawAxis( 2 ) * limitTurnSpeed ) );
        joystickRValue = ( -joystick0.getRawAxis( 1 ) - ( joystick0.getRawAxis( 2 ) * limitTurnSpeed ) );

      //Driver assists
        povMode.add( joystick0.getPOV() );
        SmartDashboard.putNumber( "POV", mode( povMode ) );
        if( povMode.size() > 10 ) {
          povMode.remove( 0 );
          if( mode( povMode ) == 0 ) {
            joystickLValue = 0.7;
            joystickRValue = 0.7;
            if( encoder1.getDistance() < encoder2.getDistance() ) {
              joystickLValue += 0.05;
            }
            if( encoder1.getDistance() > encoder2.getDistance() ) {
              joystickRValue += 0.05;
            }
          } else if( mode( povMode ) == 180 ) {
            joystickLValue = -0.7;
            joystickRValue = -0.7;
            if( encoder1.getDistance() < encoder2.getDistance() ) {
              joystickLValue -= 0.05;
            }
            if( encoder1.getDistance() > encoder2.getDistance() ) {
              joystickRValue -= 0.05;
            }
          } else if( mode( povMode ) == 90 ) {
            if( encoder1.getDistance() - encoder2.getDistance() < 140 ) {
              joystickLValue = 0.7;
              joystickRValue = -0.7;
            }
          }} else if( mode( povMode ) == 270 ) {
            if( encoder2.getDistance() - encoder1.getDistance() < 140 ) {
              joystickLValue = 0.7;
              joystickRValue = -0.7;
            }
          }
        if( joystickLValue - joystickRValue < 0.2 && joystickLValue - joystickRValue > -0.2 ) joystickLValue = joystickRValue;
        //Swap forward and back button
        if( joystick0.getRawButton( 10 ) ) {
          double temp = -joystickLValue;
          joystickLValue = -joystickRValue;
          joystickRValue = temp;

        }
        SmartDashboard.putNumber("JoystickL", joystickLValue);
        SmartDashboard.putNumber("JoystickR", joystickLValue);
        myDrive.tankDrive( joystickLValue, joystickRValue );
  }

  @Override
  public void testPeriodic() {
  }

}
