package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import jdk.swing.interop.DropTargetContextWrapper;
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
  //Color Sensor
    I2C.Port i2cPort = I2C.Port.kOnboard;
    ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    int colorCount;
    String lastColor;
  //Limit Switches
    DigitalInput drawerIn;
    DigitalInput drawerOut;
    DigitalInput startBelt;
    DigitalInput stopBelt;
  //Endcoders
    Encoder encoder1;
    Encoder encoder2;

  @Override
  public void robotInit() {
    //Init Joysticks
    joystick0 = new Joystick(0);
    joystick1 = new Joystick(1);
    //Init Drivetrain
    rbank = new Spark(0);
    lbank = new Spark(1);
    myDrive = new DifferentialDrive( lbank, rbank);
    //Other Motors
    transfer = new Spark(2);
    elevator = new Spark(3);
    outtake = new Spark(4);
    drawer = new VictorSP(9);
    intake = new VictorSPX(10);
    ctrlpnl = new VictorSPX(20);
    //Color Sensor
    lastColor = getColor();
    //Limit Switches
    drawerIn = new DigitalInput(9);
    drawerOut = new DigitalInput(8);
    startBelt = new DigitalInput(7);
    stopBelt = new DigitalInput(6);
    pullIn = false;
    //Encoders
    encoder1 = new Encoder(0, 1); //Left Encoder
    encoder2 = new Encoder(2, 3); //Right Encoder
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
   
  }

  @Override
  public void autonomousPeriodic() {
    if(drawerOut.get()) drawer.set(-0.7);
    else drawer.set(0);
  }

  public String getColor(){
        String colorString;
        double range = 0.05;
        Color detectedColor = colorSensor.getColor();
         if ((detectedColor.red <= 0.1551+range && detectedColor.red >= 0.1551-range)&&(detectedColor.green <= 0.4444+range && detectedColor.green >= 0.4444-range)&&(detectedColor.blue <= 0.4001+range && detectedColor.blue >= 0.3901-range)) {
            colorString = "Red";
         } else if ((detectedColor.red <= 0.5173+range && detectedColor.red >= 0.4073-range)&&(detectedColor.green <= 0.3488+range && detectedColor.green >= 0.3488-range)&&(detectedColor.blue <= 0.134+range && detectedColor.blue >= 0.134-range)) {
            colorString = "Blue";
         } else if ((detectedColor.red <= 0.1899+range && detectedColor.red >= 0.1899-range)&&(detectedColor.green <= 0.5598+range && detectedColor.green >= 0.5598-range)&&(detectedColor.blue <= 0.2501+range && detectedColor.blue >= 0.2501-range)) {
            colorString = "Yellow";
         } else if ((detectedColor.red <= 0.3271+range && detectedColor.red >= 0.3271-range)&&(detectedColor.green <= 0.5385+range && detectedColor.green >= 0.5385-range)&&(detectedColor.blue <= 0.134+range && detectedColor.blue >= 0.134-range)) {
            colorString = "Green";
         } else {
            colorString = "Unknown";
         }
        return colorString;
     }

  @Override
  public void teleopPeriodic() {
    //Color Sensor
      Color detectedColor = colorSensor.getColor();
      String colorString = getColor();
      DriverStation ds = DriverStation.getInstance();

      if(!lastColor.equals(colorString) && (colorString.equals("Red")||colorString.equals("Blue")||colorString.equals("Yellow")||colorString.equals("Green"))){
        colorCount++;
        lastColor = colorString;
      }
      
      //Essentially put the specified values on the smart dashboard
      SmartDashboard.putNumber("Red", detectedColor.red);
      SmartDashboard.putNumber("Green", detectedColor.green);
      SmartDashboard.putNumber("Blue", detectedColor.blue);
      SmartDashboard.putString("Detected Color", colorString);
      SmartDashboard.putNumber("colorCount", colorCount);
      SmartDashboard.putString("End Color", DriverStation.getInstance().getGameSpecificMessage());
    
    //Control Panel Manipulator
    if(joystick1.getRawButton(11)) colorCount = 0;
    if(colorCount <= 32 && joystick1.getRawButton(7)){
       ctrlpnl.set(ControlMode.PercentOutput, 0.3);
    }else if(joystick1.getRawButton(8) && !colorString.equals(ds.getGameSpecificMessage())){
       ctrlpnl.set(ControlMode.PercentOutput, 0.3);
    }else{
       ctrlpnl.set(ControlMode.PercentOutput, 0);
    }
    //Hook
      if(joystick1.getRawButton(1)) elevator.set(0.3);
      else if(joystick1.getRawButton(2)) elevator.set(-0.3);
      else elevator.set(0);
    //Gun
      //Transfer
        SmartDashboard.putBoolean("canTransfer?", startBelt.get());
        if(!startBelt.get()){
          transfer.set(-1);
          startDelay = System.currentTimeMillis();
        }else if(!stopBelt.get()){
          transfer.set(0);
        }else{
          intake.set(ControlMode.PercentOutput, -0.5);
        }
        if(System.currentTimeMillis()-startDelay>100 && !(transfer.get() == 0)) intake.set(ControlMode.PercentOutput, 0);
        else intake.set(ControlMode.PercentOutput, -0.5);
      //Outtake
      if(joystick0.getRawButton(1)){
        intake.set(ControlMode.PercentOutput, 0);
        transfer.set(-1);
        outtake.set(-(joystick0.getRawAxis(3)-1)/2);
      }else if(joystick0.getRawButton(2)){
        intake.set(ControlMode.PercentOutput, -0.5);
        transfer.set(0);
        outtake.set(0);
      }
      //Drawer +ve = in && -ve = out
        if(joystick0.getRawButton(7)) pullIn = false;
        if(joystick0.getRawButton(8)) pullIn = true;
        if(pullIn && drawerIn.get()) drawer.set(0.7);
        else if(!pullIn && drawerOut.get()) drawer.set(-0.7);
        else drawer.set(0);

    //Drive Train
      //Encoders
        SmartDashboard.putNumber("Left Encoder", encoder1.getDistance());
        SmartDashboard.putNumber("Right Encoder", encoder2.getDistance());
        if(joystick0.getRawButton(9)){
          encoder1.reset();
          encoder2.reset();
        }

        //Drive
        joystickLValue = ( -joystick0.getRawAxis( 1 ) + ( joystick0.getRawAxis( 2 ) * limitTurnSpeed ) );
        joystickRValue = ( -joystick0.getRawAxis( 1 ) - ( joystick0.getRawAxis( 2 ) * limitTurnSpeed ) );

        if(joystickLValue-joystickRValue < 0.2 && joystickLValue-joystickRValue > -0.2) joystickLValue = joystickRValue;

        myDrive.tankDrive(joystickLValue, joystickRValue);
  }

  @Override
  public void testPeriodic() {
  }
}
