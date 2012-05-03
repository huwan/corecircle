package me.corecircle;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MovementView extends SurfaceView implements SurfaceHolder.Callback {

	private int xPos[] = new int[8];// 现在x位置
	private int yPos[] = new int[8];// 现在y位置
	private static int XPos[] = new int[8];// 最终x位置
	private static int YPos[] = new int[8];// 最终y位置
	// public static int x1,y1;
	private double xVel[] = new double[8];// 随意动时的x增量
	private double yVel[] = new double[8];// 随意动时的y增量

	private static int circleRadius;// 圆点的半径
	private static int width;// 容器宽度
	private static int height;// 容器高度
	private static int Height;// view高度
	private static int Width;// view宽度
	private int d;
	private int me = width / 2;// 中心坐标位置
	private int x0;
	private int y0;

	private static int[] score = { 0, 1, 2, 3, 4, 5, 6, 4, 8 };// 分值
	private static int[] point = { 1, 4, 6, 2, 5, 7, 3, 1 };// point[7]为最终点的序号
	private static String[] CoreMember = { "ME", "A", "B", "C", "D", "E", "F",
			"G" };
	private static int shan = 0;// 控制开始闪烁
	private static int n = 7;// 好友个数
	private static int h = 0;// 模拟触屏的信号
	private static int second = 1;// 第二次触屏
	private static int push = 0;// 进行随机选择
	private static int num1 = 0;// 计数

	private Paint circlePaint;
	UpdateThread updateThread;

	public MovementView(Context context) {

		super(context);
		getHolder().addCallback(this);

		// circleRadius = 10;//设置圆点半径为15

		circlePaint = new Paint();

	}

	public MovementView(Context context, AttributeSet attrs) {

		super(context);
		getHolder().addCallback(this);

		// circleRadius = 10;//设置圆点半径为15

		circlePaint = new Paint();

	}

	@Override
	protected void onDraw(Canvas canvas) {

		int[] color = { Color.RED, Color.argb(255, 26, 251, 0),
				Color.argb(255, 255, 255, 0), Color.BLUE, Color.WHITE,
				Color.argb(255, 128, 0, 255), Color.argb(255, 255, 148, 40),
		        Color.argb(255, 255, 128, 192) };// 8种颜色
		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    	canvas.drawColor(Color.BLACK);//背景色
//    	canvas.drawColor(Color.argb(150,128,176,230));//背景色
//		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		//canvas.drawColor(Color.BLACK);// 背景色
//		Paint paint = new Paint();
//		// 从资源文件中生成位图
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.main1);
//		
//		if(bitmap!=null)
//		// 绘图
//		{canvas.drawBitmap(bitmap, 0, 0, paint);}

		// ///////////////////////////////////////////////h控制随机运动/停止//////////////////////////////////////////////////////////////////
		if (h == 1)// 小球开始随机运动
		{
			push = 0;
			for (int i = 0; i < n + 1; i++)// 绘制8个圆点
			{
				RadialGradient radGrad = new RadialGradient(xPos[i], yPos[i],
						circleRadius, color[i], color[i],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[i], yPos[i], circleRadius, circlePaint);
			}
		} else if (h == 0)// 小球停止随机运动
		{

			super.onDraw(canvas);// 绘制圆圈
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.GRAY);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(1);

			for (int j = 1; j < 9; j++) {
				canvas.drawCircle(width / 2, height / 2, (circleRadius + j
						* ((width / 2 - circleRadius) / 9)), paint);
			}

			for (int j = 1; j < n + 1; j++) {
				// 画线
				paint.setColor(color[j]);
				paint.setStrokeWidth(3);
				canvas.drawLine(width / 2, height / 2, XPos[j], YPos[j], paint);
			}

			for (int j = 1; j < n + 1; j++) {

				// 名字的绘制
				Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				Typeface mType = Typeface.create(Typeface.MONOSPACE,
						Typeface.NORMAL);
				mPaint.setColor(Color.YELLOW);
				mPaint.setTextSize(10);
				canvas.drawText(CoreMember[j - 1], xPos[j], yPos[j] + 2
						* circleRadius, mPaint);
			}

			for (int i = 0; i < n + 1; i++)// 绘制8个彩色圆点
			{
				RadialGradient radGrad = new RadialGradient(xPos[i], yPos[i],
						circleRadius, color[i], color[i],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[i], yPos[i], circleRadius, circlePaint);

			}

		}
		// ///////////////////////////////////////////push控制随机闪动/停止////////////////////////////////////////////////////////////

		if (shan == 1) {
			int nz;
			nz = num1 / 5;
			if (push < 5 * n + 5) {// 背景底图
				Paint paint = new Paint();
				for (int i = 0; i < n + 1; i++)// 绘制7个灰色圆点,7个灰色线
				{
					RadialGradient radGrad = new RadialGradient(xPos[i],
							yPos[i], circleRadius, Color.GRAY, Color.GRAY,
							Shader.TileMode.MIRROR);// 红色中心点的绘制
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[i], yPos[i], circleRadius,
							circlePaint);

					if (i < n)// 灰线的绘制
					{
						paint.setColor(Color.GRAY);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[i + 1],
								YPos[i + 1], paint);
					}

					for (int j = 1; j < n + 1; j++) {
						// 名字的绘制
						Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
						Typeface mType = Typeface.create(Typeface.MONOSPACE,
								Typeface.NORMAL);
						mPaint.setColor(Color.YELLOW);
						mPaint.setTextSize(10);
						canvas.drawText(CoreMember[j - 1], xPos[j], yPos[j] + 2
								* circleRadius, mPaint);
					}

				}
				RadialGradient radGrad = new RadialGradient(xPos[0], yPos[0],
						circleRadius, color[0], color[0],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[0], yPos[0], circleRadius, circlePaint);

				if (push < 5)// 中心红点，其余全灰
				{
					push++;
				} else if (push < 5 * (nz + 2) && push >= 5 * (nz + 1))// 点的闪动
				{

					if (nz != 0) // 还原上一个彩色点、线为灰色
					{
						radGrad = new RadialGradient(xPos[point[nz]],
								yPos[point[nz]], circleRadius, Color.GRAY,
								Color.GRAY, Shader.TileMode.MIRROR);
						circlePaint.setShader(radGrad);
						canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
								circleRadius, circlePaint);

						paint.setColor(Color.GRAY);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[point[nz]],
								YPos[point[nz]], paint);

					}

					// 当前点、线为彩色
					paint.setColor(color[point[nz]]);
					paint.setStrokeWidth(3);
					canvas.drawLine(width / 2, height / 2, XPos[point[nz]],
							YPos[point[nz]], paint);

					radGrad = new RadialGradient(xPos[point[nz]],
							yPos[point[nz]], circleRadius + 5,
							color[point[nz]], color[point[nz]],
							Shader.TileMode.MIRROR);
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
							circleRadius + 5, circlePaint);

					radGrad = new RadialGradient(xPos[0], yPos[0],
							circleRadius, color[0], color[0],
							Shader.TileMode.MIRROR);// 如果此处不重画红色点，就会看见还原和彩色的线在红点上
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[0], yPos[0], circleRadius,
							circlePaint);
				}
				num1++;
				push++;
			} else// 随机出最终结果
			{
				for (int i = 0; i < n + 1; i++)// 绘制8个彩色圆点
				{
					RadialGradient radGrad = new RadialGradient(xPos[i],
							yPos[i], circleRadius, color[i], color[i],
							Shader.TileMode.MIRROR);
					circlePaint.setShader(radGrad);
					canvas.drawCircle(xPos[i], yPos[i], circleRadius,
							circlePaint);

					Paint paint = new Paint();
					if (i > 0)// 灰线的绘制
					{
						paint.setColor(color[i]);
						paint.setStrokeWidth(3);
						canvas.drawLine(width / 2, height / 2, XPos[i],
								YPos[i], paint);
					}

				}

				for (int i = 1; i < n + 1; i++) {
					// 名字的绘制
					Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					Typeface mType = Typeface.create(Typeface.MONOSPACE,
							Typeface.NORMAL);
					mPaint.setColor(Color.YELLOW);
					mPaint.setTextSize(10);
					canvas.drawText(CoreMember[i - 1], XPos[i], YPos[i] + 2
							* circleRadius, mPaint);
				}

				RadialGradient radGrad = new RadialGradient(xPos[0], yPos[0],
						circleRadius, color[0], color[0],
						Shader.TileMode.MIRROR);// 如果此处不重画红色点，就会看见还原和彩色的线在红点上
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[0], yPos[0], circleRadius, circlePaint);

				// 最终结果圆点变大
				radGrad = new RadialGradient(xPos[point[nz]], yPos[point[nz]],
						circleRadius, color[point[nz]], color[point[nz]],
						Shader.TileMode.MIRROR);
				circlePaint.setShader(radGrad);
				canvas.drawCircle(xPos[point[nz]], yPos[point[nz]],
						circleRadius + 5, circlePaint);
			}
			// shan=0;

		}
	}

	public void updatePhysics() // 更新物理位置
	{
		int x;
		int y;
		int D;
		for (int i = 0; i < n + 1; i++) {
			if (h == 1) {
				if (second == 1)// 重新分配增量值
				{
					for (int j = 0; j < n + 1; j++) {
						double w = (Math.random() * 7);
						double angle = w * 2 * Math.PI / n;
						xVel[j] = ((Math.cos(angle)) * (circleRadius / 2));
						yVel[j] = ((Math.sin(angle)) * (circleRadius / 2));
					}

					// 重新产生随机顺序，赋值给point数组
					for (int m = 0; m < n; m++) {
						point[m] = m + 1;
					}
					Random random = new Random();
					for (int m = 0; i < n; i++) {
						int p = random.nextInt(n);
						int tmp = point[m];
						point[m] = point[p];
						point[p] = tmp;
					}
					random = null;

					second = 0;

				} else {
					xPos[i] += xVel[i];
					yPos[i] += yVel[i];
				}
			} else if (h == 0) {
				for (int k = 1; k < n + 1; k++) {
					D = circleRadius + d * (9 - score[k - 1]); // y=(int)
																// ((Math.sin(Math.PI*s/i))*D);
					double angle = k * 2 * Math.PI / n;
					x = (int) ((Math.cos(angle)) * D);
					y = (int) ((Math.sin(angle)) * D);

					XPos[k] = x + width / 2;
					YPos[k] = y + height / 2;
				}

				xPos[i] = XPos[i];
				yPos[i] = YPos[i];
				xVel[i] = 0;
				yVel[i] = 0;

			}

			if (yPos[i] - circleRadius < 0 || yPos[i] + circleRadius > height) // 判断小球是否碰边
			{
				if (yPos[i] - circleRadius < 0) {
					yPos[i] = circleRadius;
				} else {
					yPos[i] = height - circleRadius;
				}
				yVel[i] *= -1;
			}
			if (xPos[i] - circleRadius < 0 || xPos[i] + circleRadius > width) {
				if (xPos[i] - circleRadius < 0) {
					xPos[i] = circleRadius;
				} else {
					xPos[i] = width - circleRadius;
				}
				xVel[i] *= -1;
			}

		}

	}

	public void surfaceCreated(SurfaceHolder holder) {

		Rect surfaceFrame = holder.getSurfaceFrame();

		width = surfaceFrame.width();
		height = surfaceFrame.height();

		Height = getHeight();
		Width = getWidth();
		if (width > height) {
			width = surfaceFrame.height();
			height = surfaceFrame.width();
			Height = getWidth();
			Width = getHeight();
		}

		// d=(width/2-circleRadius)/9;//每圈的间隔宽度
		d = (width / 2) / 9;// 每圈的间隔宽度
		circleRadius = d / 2;

		int n1 = 0;
		int n2 = 0;
		Random rand = new Random();

		for (int i = 0; i < n + 1; i++)// 初始化小球最初坐标
		{
			switch (i) {
			case 0: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2 - circleRadius);
				break;
			}
			case 1: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2 - circleRadius);
				break;
			}
			case 2: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2);
				break;
			}
			case 3: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2);
				break;
			}
			case 4: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 5: {
				n1 = rand.nextInt(width / 2 - circleRadius);
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 6: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			case 7: {
				n1 = rand.nextInt(width) + width / 2;
				n2 = rand.nextInt(height / 2) + height / 2;
				break;
			}
			}
			xPos[i] = n1;
			yPos[i] = n2;
		}
		XPos[0] = width / 2;
		YPos[0] = height / 2;

		updateThread = new UpdateThread(this);
		updateThread.setRunning(true);
		updateThread.start();

	}

	// }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		boolean retry = true;

		updateThread.setRunning(false);
		while (retry) {
			try {
				updateThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public static String CheckInOut(int x0, int y0)// 判断触点是否在圆点内
	{

		if ((y0 > (Height - height)) && (x0 > (Width - width))) {
			for (int i = 1; i < n + 1; i++) {
				if ((x0 - XPos[i]) * (x0 - XPos[i]) + (y0 - YPos[i])
						* (y0 - YPos[i]) - circleRadius * 9 / 4 * circleRadius < 0)
					return CoreMember[i - 1];
			}
			return "IN";
		}
		return "NULL";// 如果不在区域内或者任意圆点内，都返回字符串"NULL";
	}

	public static void RefreshMove(int _n, int[] _score, String[] _member)

	{

		h = 1;
		second = 1;
		shan = 0;
		num1 = 0;
		n = _n;
		// push=0;
		score = _score;
		CoreMember = _member;
	}

	public static void RefreshStop() {
		h = 0;
		second = 0;
	}

	public static void RefreshRadom(int _totalnum, int _recommember) {
		RefreshStop();
		point[_totalnum] = _recommember + 1;
		shan = 1;

	}

	public static int ReturnWait() {
		if (push < 5 * n + 5)
			return 0;
		else
			return 1;
	}

	public static void Push() {
		push = 0;
	}

}
