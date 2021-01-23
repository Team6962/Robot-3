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

import java.io.FileWriter;
import java.io.File;

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

  //Encoders
    Encoder encoder1;
    Encoder encoder2;


  //Replay values
  int clock;
  long start;
  ArrayList<int[]> path;
  double correctionFactor;

  //Testing values
    int counter = 0;
    int infLoopProt = 0;
  
  // Camera
    UsbCamera camera;
    double[] targetAngleValue = new double[ 1 ];
    boolean[] setTargetAngleValue = new boolean[ 1 ];
    double[] ballAngleValue = new double[ 1 ]; 
    boolean[] setBallAngleValue = new boolean[ 1 ];

  public static final int WINDOW_WIDTH = 1280;
  public static final int WINDOW_HEIGHT = 720;

  // private Object imgLock = new Object();
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
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {

    start = System.currentTimeMillis();
    clock = 500;
    correctionFactor = 0.25;
    path = new ArrayList<int[]>();

    Scanner sc;
    try {
        sc = new Scanner(new File("[FILEPATH]"));
    } catch (Exception e) {
        sc = new Scanner("0,0\n0,5\n10,20\n20,25\n30,30");
    }
    sc.useDelimiter(",");
    while (sc.hasNext()) {
        path.add(new int[] { sc.nextInt(), sc.nextInt() });
    }
    sc.close();
   
  }

  @Override
  public void autonomousPeriodic() {

    long now = System.currentTimeMillis();
    int step = (int) Math.floor(now / clock); // index of path we're on or going through
    double substep = (now % clock) / clock; // % of the way through current path step
    
    if (step < path.size() - 1) {
        int[] current = path.get(step);
        int[] next = path.get(step + 1);

        double targetLeft = (next[0] - current[0]) * substep;
        double realLeft = encoder1.getDistance() - current[0];
        if (realLeft <= 0)
            realLeft = 0.01;
        double correctionLeft = targetLeft / realLeft;
        correctionLeft = ((correctionLeft - 1) * correctionFactor) + 1;

        double targetRight = (next[1] - current[1]) * substep;
        double realRight = encoder2.getDistance() - current[1];
        if (realRight <= 0)
            realRight = 0.01;
        double correctionRight = targetRight / realRight;
        correctionRight = ((correctionRight - 1) * correctionFactor) + 1;

        myDrive.tankDrive(correctionLeft, correctionRight);
    } else if (step < path.size()) {
        int[] target = path.get(step);

        double targetLeft = target[0] * substep;
        double realLeft = encoder1.getDistance();
        if (realLeft <= 0)
            realLeft = 0.1;
        double correctionLeft = targetLeft / realLeft;
        correctionLeft = ((correctionLeft - 1) * correctionFactor) + 1;

        double targetRight = target[1] * substep;
        double realRight = encoder2.getDistance();
        if (realRight <= 0)
            realRight = 0.1;
        double correctionRight = targetRight / realRight;
        correctionRight = ((correctionRight - 1) * correctionFactor) + 1;

        myDrive.tankDrive(correctionLeft, correctionRight);
    } else {
        myDrive.tankDrive(0, 0);
    }


    //Disabled because challenge requires driving, not shooting
    /*
    if( drawerOut.get() ) drawer.set( -0.7 ); //If the switch indicating that the drawer isn't all the way out isn't clicked, then make the drawer move out.
    else drawer.set( 0 ); //Else, stop moving.
    */
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
       
        } else colorString = "Unknown";

        //Returns the detected and interpreted colour.
        return colorString;

  }

  public static int mode( ArrayList<Integer> il ) {

      //Returns the mode of a data set, returns -1 if there is more than one mode.
      ArrayList<Integer> occurences = new ArrayList<>();

      for( int i = 0; i < il.size(); i++ ) {

          //Goes through each item in the list.
          int c = 0;
          for( int j = 0; j < il.size(); j++ ) {

              //Compare each il[ i ] to il[ j ],
              //look for equality and count for each ==.
              if( il.get( i ) == il.get( j ) ) c++;

          }
          //Assign the count to a new cell in occurences.
          occurences.add( c );

      }
      //For each int in occurences, which corresponds to the ints in il,
      int d = 0;
      //If it's the largest number in occurences, then add 1 to d.
      for( int k : occurences ) if( k == largestIn( occurences ) ) d++;
      //If the number of highest number in occurences exceeds
      //the highest number in occurences, return -1,
      //as that indicates more than 1 mode.
      if( largestIn( occurences ) < d ) return -1;
      //Effectively else, return the mode.
      return il.get( indexOf( largestIn( occurences ), occurences ) );

  }

  public static int largestIn( ArrayList<Integer> il ) {

      //Returns the largest int in ArrayList<Integer> il.
      assert il.size() > 0: "List has a length less than 1.";
      int i = il.get( 0 );
      for( int j : il ) if( j > i ) i = j;
      return i;

  }

  public static int indexOf( int j, ArrayList<Integer> il ) {

      //Returns the index of j in il, -1 if j is not in il.
      for( int i = 0; i < il.size(); i++ ) if( il.get( i ) == j ) return i;
      return -1;

  }

  public void recordValues(Encoder encoder1, Encoder encoder2, ArrayList<String[]> arraylist) {
    long now = System.currentTimeMillis();
    if ((now-start) % 50 == 0) {
      arraylist.add(new String[] {String.valueOf(encoder1.getDistance()), String.valueOf(encoder2.getDistance())});
    }
  }

  public void saveValues(ArrayList<String[]> arrayList) {
    System.out.println("Saving values.");
    try {			
        FileWriter csvWriter = new FileWriter("test.csv");
        csvWriter.append("Links, AI Summary \n");
         
        for (int i = 0; i <arrayList.size(); i++) {
          for (int j = 0; j <arrayList.get(i).length; j++) {
            csvWriter.append(arrayList.get(i)[j]);
              
            if (j !=arrayList.get(i).length-1) {
              csvWriter.append(", ");
            }
          }
          csvWriter.append("\n");
        }
          
        csvWriter.flush();
        csvWriter.close();
    }
     catch (Exception e) {
      System.out.println(e + "ERROR IN WRITING FILE.");
    }
    System.out.println("DONE WRITING FILE.");
  }

  @Override
  public void teleopInit() {
    
    start = System.currentTimeMillis();

  }


  @Override
  public void teleopPeriodic() {
      
    //Color Sensor
      Color detectedColor = colorSensor.getColor();
      String colorString = getColor();
      DriverStation ds = DriverStation.getInstance();
      ArrayList<String[]> dataValues = new ArrayList<String[]>();


      recordValues(encoder1, encoder2, dataValues);
      if ( joystick1.getRawButton(12) && (infLoopProt == 0)) {
        saveValues(dataValues);
        infLoopProt++;
      }
      
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
    
    //Swap forward and back button
      if( joystick0.getRawButton( 10 ) ) {

        double temp = -joystickLValue;
        joystickLValue = -joystickRValue;
        joystickRValue = temp;

      }

    //Control Panel Manipulator
      if( joystick1.getRawButton( 11 ) ) colorCount = 0;
      if( colorCount <= 32 && joystick1.getRawButton( 7 ) ) {

        ctrlpnl.set( ControlMode.PercentOutput, 0.3 );

      } else if( joystick1.getRawButton( 8 ) && !colorString.equals( ds.getGameSpecificMessage() ) ) {

        ctrlpnl.set( ControlMode.PercentOutput, 0.3 );

      } else ctrlpnl.set( ControlMode.PercentOutput, 0 );
    
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
        
        } else if( !stopBelt.get() ) transfer.set( 0 );
        else intake.set( ControlMode.PercentOutput, -0.75 );

        if( !( transfer.get() == 0 ) ) intake.set( ControlMode.PercentOutput, -0.5 );
        else intake.set( ControlMode.PercentOutput, -0.75 );

      //Outtake
        if( joystick0.getRawButton( 1 ) ) {
        
          intake.set( ControlMode.PercentOutput, 0 );
          transfer.set( -1 );
          counter = 0;
        
        } else if( joystick0.getRawButton( 2 ) ) {
        
          intake.set( ControlMode.PercentOutput, -0.75 );
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

            joystickLValue = 0.5;
            joystickRValue = 0.5;
            if( encoder1.getDistance() < encoder2.getDistance() ) joystickLValue += 0.05;
            if( encoder1.getDistance() > encoder2.getDistance() ) joystickRValue += 0.05;

          } else if( mode( povMode ) == 180 ) {

            joystickLValue = -0.5;
            joystickRValue = -0.5;
            if( encoder1.getDistance() < encoder2.getDistance() ) joystickLValue -= 0.05;
            if( encoder1.getDistance() > encoder2.getDistance() ) joystickRValue -= 0.05;

          } else if( mode( povMode ) == 90 ) {

            if( encoder1.getDistance() - encoder2.getDistance() < 140 ) {
              
              joystickLValue = 0.5;
              joystickRValue = -0.5;

            }

          }

        }
        if( joystickLValue - joystickRValue < 0.2 && joystickLValue - joystickRValue > -0.2 ) joystickLValue = joystickRValue;
        myDrive.tankDrive( joystickLValue, joystickRValue );

  }

  @Override
  public void testPeriodic() {
  }

}
