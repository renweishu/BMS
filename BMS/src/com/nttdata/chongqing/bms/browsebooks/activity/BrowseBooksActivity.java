package com.nttdata.chongqing.bms.browsebooks.activity;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nttdata.chongqing.bms.R;
import com.nttdata.chongqing.bms.browsebooks.util.ShowImageUtil;
import com.nttdata.chongqing.bms.newbookonline.activity.NewBookOnlineActivity;

public class BrowseBooksActivity extends Activity
implements android.view.GestureDetector.OnGestureListener {

	// 随即播放
	private TextView txt_PlayStyle_Random;
	// 顺序播放
	private TextView txt_PlayStyle_Sequence;
	// 记录播放方式
	private TextView txt_play_Record;
	// 当前页数
	private TextView txt_NowPage;
	// 总页数
	private TextView txt_TotailPage;
	// 播放按钮
	private TextView txt_Play;
	// 播放底板
	private TextView txt_backGround;
	// 记录手势类
	private GestureDetector gestureDetector;
	// 触摸画面时判定标记
	private boolean isTouchFlag = true;
	// 播放判定标记
	private boolean isPlayFlag = false;
	// 加载图片工具类
	private ShowImageUtil sImageUtil;
	// 加载图片试图类
	private ImageView imageView ;
	// 声明AdapterViewFlipper类
	private AdapterViewFlipper flipper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_browsebooks);
		flipper = (AdapterViewFlipper) findViewById(R.id.flipper);
		gestureDetector = new GestureDetector(this, this);

		// 获取播放控件
		txt_Play = (TextView) findViewById(R.id.Play);
		txt_Play.setClickable(true);
		// 获取随即播放控件
		txt_PlayStyle_Random = (TextView) findViewById(R.id.Txt_playStyle1);
		txt_PlayStyle_Random.setClickable(true);
		// 获取顺序播放控件
		txt_PlayStyle_Sequence = (TextView) findViewById(R.id.Txt_playStyle2);
		txt_PlayStyle_Sequence.setClickable(true);
		// 默认方式为顺序播放
		txt_PlayStyle_Sequence.setTextColor(Color.RED);
		// 获取播放底板
		txt_backGround = (TextView) findViewById(R.id.Txt_backGround);
		txt_backGround.setEnabled(false);
		// 获取当前页显示控件
		txt_NowPage = (TextView) findViewById(R.id.Txt_NowPage);
		// 获取总页显示控件
		txt_TotailPage = (TextView) findViewById(R.id.Txt_TotailPage);	  	
		// 获取图片资源
		sImageUtil = new ShowImageUtil();
		Intent int0 = getIntent();
		String pp = int0.getExtras().getString("path");
		String fn = int0.getExtras().getString("saveFolder");
		try {
			sImageUtil.getList(pp, fn);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		if (sImageUtil.getFilePathList() == null) {
			// 显示页数为0
			txt_NowPage.setText(getResources().getString(R.string.stringZero));
			txt_TotailPage.setText(getResources().getString(R.string.stringZero));
			setInvisibleStyle();
			toastShow("该目录下没有杂志存在，请确认");

		} else {
			// 获取总页数        
			txt_TotailPage.setText(""+(sImageUtil.getFilePathList().size())+"");
		}

		// 记录播放方式初始化
		txt_play_Record = new TextView(this);
		txt_play_Record.setText("");

		// 返回按钮
		TextView txt_back = (TextView) findViewById(R.id.back);
		txt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 创建适配器，给AdapterViewFlipper中的ImageView添加资源
		BaseAdapter adapter = new BaseAdapter()
		{
			@Override
			public int getCount()
			{
				if (sImageUtil.getFilePathList() == null) {
					return 0;
				} else {
					return sImageUtil.getFilePathList().size();            		
				}

			}

			@Override
			public Object getItem(int position)
			{
				return position;
			}

			@Override
			public long getItemId(int position)
			{
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				imageView = new ImageView(BrowseBooksActivity.this);
				if (sImageUtil.getFilePathList() != null) {
					sImageUtil.setImageView(imageView, txt_play_Record.getText().toString(),
							getResources().getString(R.string.playStyle_Random), position, txt_NowPage);                   	
				}
				//  把图片不按比例扩大/缩小到View的大小显示
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				return imageView;
			}
		};
		flipper.setAdapter(adapter);

		// 随即播放控件点击事件
		txt_PlayStyle_Random.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isPlayFlag) {
					// 停止播放
					flipper.setAutoStart(false);
					flipper.stopFlipping();
					txt_Play.setBackgroundResource(R.drawable.play);
				} 
				txt_PlayStyle_Random.setTextColor(Color.RED);
				txt_PlayStyle_Sequence.setTextColor(Color.WHITE);
				txt_play_Record.setText(R.string.playStyle_Random);
			}
		});

		// 顺序播放控件点击事件
		txt_PlayStyle_Sequence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isPlayFlag) {
					// 停止播放
					flipper.setAutoStart(false);
					flipper.stopFlipping();
					txt_Play.setBackgroundResource(R.drawable.play);
				} 
				txt_PlayStyle_Random.setTextColor(Color.WHITE);
				txt_PlayStyle_Sequence.setTextColor(Color.RED);
				txt_play_Record.setText(R.string.playStyle_Sequence);
			}
		});

		// 双击屏幕事件
		gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

			@Override
			public boolean onSingleTapConfirmed(MotionEvent arg0) {
				return false;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent arg0) {
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent arg0) {

				if (isTouchFlag) {
					setInvisibleStyle();
					isTouchFlag = false;
				} else {
					setVisibleStyle();
					isTouchFlag = true;                 
				}
				return false;
			}
		});
	}

	@Override  
	public boolean onTouchEvent(MotionEvent event) {  

		isPlayFlag = false;
		flipper.setAutoStart(false);
		flipper.stopFlipping();
		txt_Play.setBackgroundResource(R.drawable.play);
		return gestureDetector.onTouchEvent(event);    
	} 

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {

		// 从左向右滑动
		if (arg1.getX() - arg0.getX() > 120) {
			// 上一张图片
			flipper.showPrevious();
			return true;
			//从右向左滑动
		} else if (arg1.getX() - arg0.getX() < -120) {
			// 下一张图片
			flipper.showNext();  
			return true;       
		}  
		return true; 
	}

	@Override
	public void onLongPress(MotionEvent arg0) {  
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	public void auto(View source) {

		if (sImageUtil.getFilePathList() != null) {
			if (isPlayFlag) {
				txt_Play.setBackgroundResource(R.drawable.play);
				isPlayFlag = false;
				// 不会自动播放
				flipper.setAutoStart(false);
				// 停止播放
				flipper.stopFlipping();
			} else {
				txt_Play.setBackgroundResource(R.drawable.stop);
				isPlayFlag = true;
				// 开始播放
				flipper.startFlipping();
			}
		} else {
			txt_Play.setEnabled(false);
			txt_PlayStyle_Random.setEnabled(false);
			txt_PlayStyle_Sequence.setEnabled(false);
		}
	}

	private void toastShow(String text) {
		Toast.makeText(BrowseBooksActivity.this, text, Toast.LENGTH_LONG).show();
	}

	private void setInvisibleStyle() {
		// 不可见
		txt_Play.setVisibility(View.INVISIBLE);
		txt_PlayStyle_Random.setVisibility(View.INVISIBLE);
		txt_PlayStyle_Sequence.setVisibility(View.INVISIBLE);
		txt_backGround.setVisibility(View.INVISIBLE);
	}

	private void setVisibleStyle() {
		// 可见
		txt_Play.setVisibility(View.VISIBLE);
		txt_PlayStyle_Random.setVisibility(View.VISIBLE);
		txt_PlayStyle_Sequence.setVisibility(View.VISIBLE);
		txt_backGround.setVisibility(View.VISIBLE);
	}
}
