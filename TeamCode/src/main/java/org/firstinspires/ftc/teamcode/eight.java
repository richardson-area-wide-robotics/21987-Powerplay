package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="88", group="Main")
public class eight extends OpMode {
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor driveFrontLeft;
    private DcMotor driveBackLeft;
    private DcMotor driveFrontRight;
    private DcMotor driveBackRight;

    private Servo intake;

    private DcMotor armLeft;
    private DcMotor armRight;

    private int leftArmPos, rightArmPos;
    private double servoPos;

    private final int[] armPositions = { 0, 190, 250, 440 };
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        // init hardware variables.
        driveFrontLeft  = hardwareMap.get(DcMotor.class, "driveFrontLeft");
        driveBackLeft   = hardwareMap.get(DcMotor.class, "driveBackLeft");
        driveFrontRight = hardwareMap.get(DcMotor.class, "driveFrontRight");
        driveBackRight  = hardwareMap.get(DcMotor.class, "driveBackRight");
        intake = hardwareMap.get(Servo.class, "intake");
        armLeft = hardwareMap.get(DcMotor.class, "armLeft");
        armRight = hardwareMap.get(DcMotor.class, "armRight");

        /*
         * Set up Drive Motors
         */
        driveFrontRight.setDirection(DcMotor.Direction.REVERSE);
        driveBackRight.setDirection(DcMotor.Direction.REVERSE);
        driveFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        driveBackLeft.setDirection(DcMotor.Direction.FORWARD);

        /*
         * Set up Arm Motors
         */

        // directions should be good?
        leftArmPos = rightArmPos = 0;

        armLeft.setDirection(DcMotor.Direction.FORWARD);
        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armRight.setDirection(DcMotor.Direction.REVERSE);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*
         * Set up Intake Servo
         */

        // i'm not gonna lie, i have no idea what to do here
        servoPos = 0;
        intake.setDirection(Servo.Direction.FORWARD);


        // telemetry
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        telemetry.addData("Left Arm Position", armLeft.getCurrentPosition());
        telemetry.addData("Right Arm Position", armRight.getCurrentPosition());
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        armLeft.setTargetPosition(0);
        armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armLeft.setPower(1.0);

        armRight.setTargetPosition(0);
        armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armRight.setPower(1.0);

        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // move robot
        drive();

        // move arm & intake
        lift();
        intake();

        // telemetry
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Left Arm Position", armLeft.getCurrentPosition());
        telemetry.addData("Right Arm Position", armRight.getCurrentPosition());
        telemetry.addData("Servo Position", intake.getPosition());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        driveFrontLeft.setPower(0);
        driveBackLeft.setPower(0);
        driveFrontRight.setPower(0);
        driveBackRight.setPower(0);

        armLeft.setPower(0);
        armRight.setPower(0);
    }

    private void drive() {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        driveFrontLeft.setPower(frontLeftPower);
        driveBackLeft.setPower(backLeftPower);
        driveFrontRight.setPower(frontRightPower);
        driveBackRight.setPower(backRightPower);
    }

    private void lift() {
        int step = 10;
        if (gamepad1.a) {
            leftArmPos = rightArmPos = armPositions[0];
        }
        else if (gamepad1.b) {
            leftArmPos = rightArmPos = armPositions[1];
        }
        else if (gamepad1.x) {
            leftArmPos = rightArmPos = armPositions[2];
        }
        else if (gamepad1.y) {
            leftArmPos = rightArmPos = armPositions[3];
        }
        else if (gamepad1.left_trigger > 0.2) {
            leftArmPos = armLeft.getCurrentPosition() - step;
            rightArmPos = armRight.getCurrentPosition() - step;
        }
        else if (gamepad1.right_trigger > 0.2) {
            leftArmPos = armLeft.getCurrentPosition() + step;
            rightArmPos = armRight.getCurrentPosition() + step;

        }

        armLeft.setTargetPosition(Range.clip(leftArmPos, 0, 441));
        armRight.setTargetPosition(Range.clip(rightArmPos, 0, 441));
    }

    private void intake() {
        double step = .1;

        if (gamepad1.left_bumper) {
            servoPos -= step;
        }
        else if (gamepad1.right_bumper) {
            servoPos += step;
        }

        servoPos = Range.clip(servoPos, 0, 1);
        intake.setPosition(servoPos);
    }
}
