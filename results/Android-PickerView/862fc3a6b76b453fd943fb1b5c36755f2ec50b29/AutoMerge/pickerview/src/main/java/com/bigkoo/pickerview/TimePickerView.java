package com.bigkoo.pickerview;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelTime;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器
 * Created by Sai on 15/11/22.
 * Updated by XiaoSong on 2017-2-22.
 */
public class TimePickerView extends BasePickerView implements View.OnClickListener {

<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/pickerview/src/main/java/com/bigkoo/pickerview/TimePickerView.java
  private int layoutRes;
=======
  private WheelView.DividerType dividerType;
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/pickerview/src/main/java/com/bigkoo/pickerview/TimePickerView.java


  public enum Type {
    ALL,
    YEAR_MONTH_DAY,
    HOURS_MINS,
    MONTH_DAY_HOUR_MIN,
    YEAR_MONTH,
    YEAR_MONTH_DAY_HOUR_MIN
  }

  private CustomListener customListener;

  WheelTime wheelTime;

  private Button btnSubmit, btnCancel;

  private TextView tvTitle;

  private OnTimeSelectListener timeSelectListener;

  private int gravity = Gravity.CENTER;

  private TimePickerView.Type type;

  private String Str_Submit;

  private String Str_Cancel;

  private String Str_Title;

  private int Color_Submit;

  private int Color_Cancel;

  private int Color_Title;

  private int Color_Background_Wheel;

  private int Color_Background_Title;

  private int Size_Submit_Cancel;

  private int Size_Title;

  private int Size_Content;

  private Calendar date;

  private Calendar startDate;

  private Calendar endDate;

  private int startYear;

  private int endYear;

  private boolean cyclic;

  private boolean cancelable;

  private int textColorOut;

  private int textColorCenter;

  private int dividerColor;

  private float lineSpacingMultiplier = 1.6F;

  private boolean isDialog;

  private String label_year, label_month, label_day, label_hours, label_mins, label_seconds;

  private static final String TAG_SUBMIT = "submit";

  private static final String TAG_CANCEL = "cancel";

  public TimePickerView(Builder builder) {
    super(builder.context);
    this.timeSelectListener = builder.timeSelectListener;
    this.gravity = builder.gravity;
    this.type = builder.type;
    this.Str_Submit = builder.Str_Submit;
    this.Str_Cancel = builder.Str_Cancel;
    this.Str_Title = builder.Str_Title;
    this.Color_Submit = builder.Color_Submit;
    this.Color_Cancel = builder.Color_Cancel;
    this.Color_Title = builder.Color_Title;
    this.Color_Background_Wheel = builder.Color_Background_Wheel;
    this.Color_Background_Title = builder.Color_Background_Title;
    this.Size_Submit_Cancel = builder.Size_Submit_Cancel;
    this.Size_Title = builder.Size_Title;
    this.Size_Content = builder.Size_Content;
    this.startYear = builder.startYear;
    this.endYear = builder.endYear;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.date = builder.date;
    this.cyclic = builder.cyclic;
    this.cancelable = builder.cancelable;
    this.label_year = builder.label_year;
    this.label_month = builder.label_month;
    this.label_day = builder.label_day;
    this.label_hours = builder.label_hours;
    this.label_mins = builder.label_mins;
    this.label_seconds = builder.label_seconds;
    this.textColorCenter = builder.textColorCenter;
    this.textColorOut = builder.textColorOut;
    this.dividerColor = builder.dividerColor;
    this.customListener = builder.customListener;
    this.layoutRes = builder.layoutRes;
    this.isDialog = builder.isDialog;
    this.dividerType = builder.dividerType;
    initView(builder.context);
  }

  public static class Builder {

<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/pickerview/src/main/java/com/bigkoo/pickerview/TimePickerView.java
    private int layoutRes = R.layout.pickerview_time;
=======
    private WheelView.DividerType dividerType;
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/pickerview/src/main/java/com/bigkoo/pickerview/TimePickerView.java


    private Context context;

    private CustomListener customListener;

    private OnTimeSelectListener timeSelectListener;

    private TimePickerView.Type type = Type.ALL;

    private int gravity = Gravity.CENTER;

    private String Str_Submit;

    private String Str_Cancel;

    private String Str_Title;

    private int Color_Submit;

    private int Color_Cancel;

    private int Color_Title;

    private int Color_Background_Wheel;

    private int Color_Background_Title;

    private int Size_Submit_Cancel = 17;

    private int Size_Title = 18;

    private int Size_Content = 18;

    private Calendar date;

    private Calendar startDate;

    private Calendar endDate;

    private int startYear;

    private int endYear;

    private boolean cyclic = false;

    private boolean cancelable = true;

    private int textColorOut;

    private int textColorCenter;

    private int dividerColor;

    private float lineSpacingMultiplier = 1.6F;

    private boolean isDialog;

    private String label_year, label_month, label_day, label_hours, label_mins, label_seconds;

    public Builder(Context context, OnTimeSelectListener listener) {
      this.context = context;
      this.timeSelectListener = listener;
    }

    public Builder setType(TimePickerView.Type type) {
      this.type = type;
      return this;
    }

    public Builder gravity(int gravity) {
      this.gravity = gravity;
      return this;
    }

    public Builder setSubmitText(String Str_Submit) {
      this.Str_Submit = Str_Submit;
      return this;
    }

    public Builder isDialog(boolean isDialog) {
      this.isDialog = isDialog;
      return this;
    }

    public Builder setCancelText(String Str_Cancel) {
      this.Str_Cancel = Str_Cancel;
      return this;
    }

    public Builder setTitleText(String Str_Title) {
      this.Str_Title = Str_Title;
      return this;
    }

    public Builder setSubmitColor(int Color_Submit) {
      this.Color_Submit = Color_Submit;
      return this;
    }

    public Builder setCancelColor(int Color_Cancel) {
      this.Color_Cancel = Color_Cancel;
      return this;
    }

    public Builder setBgColor(int Color_Background_Wheel) {
      this.Color_Background_Wheel = Color_Background_Wheel;
      return this;
    }

    public Builder setTitleBgColor(int Color_Background_Title) {
      this.Color_Background_Title = Color_Background_Title;
      return this;
    }

    public Builder setTitleColor(int Color_Title) {
      this.Color_Title = Color_Title;
      return this;
    }

    public Builder setSubCalSize(int Size_Submit_Cancel) {
      this.Size_Submit_Cancel = Size_Submit_Cancel;
      return this;
    }

    public Builder setTitleSize(int Size_Title) {
      this.Size_Title = Size_Title;
      return this;
    }

    public Builder setContentSize(int Size_Content) {
      this.Size_Content = Size_Content;
      return this;
    }

    public Builder setDate(Calendar date) {
      this.date = date;
      return this;
    }

    public Builder setLayoutRes(int res, CustomListener customListener) {
      this.layoutRes = res;
      this.customListener = customListener;
      return this;
    }

    public Builder setRange(int startYear, int endYear) {
      this.startYear = startYear;
      this.endYear = endYear;
      return this;
    }

    /**
         * 设置起始时间
         *
         * @return
         */
    public Builder setRangDate(Calendar startDate, Calendar endDate) {
      this.startDate = startDate;
      this.endDate = endDate;
      return this;
    }

    /**
         * 设置间距倍数,但是只能在1.2-2.0f之间
         *
         * @param lineSpacingMultiplier
         */
    public Builder setLineSpacingMultiplier(float lineSpacingMultiplier) {
      this.lineSpacingMultiplier = lineSpacingMultiplier;
      return this;
    }

    /**
         * 设置分割线的颜色
         *
         * @param dividerColor
         */
    public Builder setDividerColor(int dividerColor) {
      this.dividerColor = dividerColor;
      return this;
    }

    /**
         * 设置分割线的类型
         *
         * @param dividerType
         */
    public Builder setDividerType(WheelView.DividerType dividerType) {
      this.dividerType = dividerType;
      return this;
    }

    /**
         * 设置分割线之间的文字的颜色
         *
         * @param textColorCenter
         */
    public Builder setTextColorCenter(int textColorCenter) {
      this.textColorCenter = textColorCenter;
      return this;
    }

    /**
         * 设置分割线以外文字的颜色
         *
         * @param textColorOut
         */
    public Builder setTextColorOut(int textColorOut) {
      this.textColorOut = textColorOut;
      return this;
    }

    public Builder isCyclic(boolean cyclic) {
      this.cyclic = cyclic;
      return this;
    }

    public Builder setOutSideCancelable(boolean cancelable) {
      this.cancelable = cancelable;
      return this;
    }

    public Builder setLabel(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
      this.label_year = label_year;
      this.label_month = label_month;
      this.label_day = label_day;
      this.label_hours = label_hours;
      this.label_mins = label_mins;
      this.label_seconds = label_seconds;
      return this;
    }

    public TimePickerView build() {
      return new TimePickerView(this);
    }
  }

  private void initView(Context context) {
    initViews();
    init();
    initEvents();
    if (customListener == null) {
      LayoutInflater.from(context).inflate(R.layout.pickerview_time, contentContainer);
      tvTitle = (TextView) findViewById(R.id.tvTitle);
      btnSubmit = (Button) findViewById(R.id.btnSubmit);
      btnCancel = (Button) findViewById(R.id.btnCancel);
      btnSubmit.setTag(TAG_SUBMIT);
      btnCancel.setTag(TAG_CANCEL);
      btnSubmit.setOnClickListener(this);
      btnCancel.setOnClickListener(this);
      btnSubmit.setText(TextUtils.isEmpty(Str_Submit) ? context.getResources().getString(R.string.pickerview_submit) : Str_Submit);
      btnCancel.setText(TextUtils.isEmpty(Str_Cancel) ? context.getResources().getString(R.string.pickerview_cancel) : Str_Cancel);
      tvTitle.setText(TextUtils.isEmpty(Str_Title) ? "" : Str_Title);
      btnSubmit.setTextColor(Color_Submit == 0 ? pickerview_timebtn_nor : Color_Submit);
      btnCancel.setTextColor(Color_Cancel == 0 ? pickerview_timebtn_nor : Color_Cancel);
      tvTitle.setTextColor(Color_Title == 0 ? pickerview_topbar_title : Color_Title);
      btnSubmit.setTextSize(Size_Submit_Cancel);
      btnCancel.setTextSize(Size_Submit_Cancel);
      tvTitle.setTextSize(Size_Title);
      RelativeLayout rv_top_bar = (RelativeLayout) findViewById(R.id.rv_topbar);
      rv_top_bar.setBackgroundColor(Color_Background_Title == 0 ? pickerview_bg_topbar : Color_Background_Title);
    } else {
      customListener.customLayout(LayoutInflater.from(context).inflate(layoutRes, contentContainer));
    }
    LinearLayout timePickerView = (LinearLayout) findViewById(R.id.timepicker);
    timePickerView.setBackgroundColor(Color_Background_Wheel == 0 ? bgColor_default : Color_Background_Wheel);
    wheelTime = new WheelTime(timePickerView, type, gravity, Size_Content);
    if (startYear != 0 && endYear != 0 && startYear <= endYear) {
      setRange();
    }
    if (startDate != null && endDate != null) {
      if (startDate.getTimeInMillis() < endDate.getTimeInMillis()) {
        setRangDate();
      }
    } else {
      if (startDate != null && endDate == null) {
        setRangDate();
      } else {
        if (startDate == null && endDate != null) {
          setRangDate();
        }
      }
    }
    setTime();
    wheelTime.setLabels(label_year, label_month, label_day, label_hours, label_mins, label_seconds);
    setOutSideCancelable(cancelable);
    wheelTime.setCyclic(cyclic);
    wheelTime.setDividerColor(dividerColor);
    wheelTime.setDividerType(dividerType);
    wheelTime.setLineSpacingMultiplier(lineSpacingMultiplier);
    wheelTime.setTextColorOut(textColorOut);
    wheelTime.setTextColorCenter(textColorCenter);
  }

  /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
  private void setRange() {
    wheelTime.setStartYear(startYear);
    wheelTime.setEndYear(endYear);
  }

  /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
  private void setRangDate() {
    wheelTime.setRangDate(startDate, endDate);
    if (startDate != null && endDate != null) {
      if (date != null) {
        if (date.getTimeInMillis() > startDate.getTimeInMillis() && date.getTimeInMillis() < endDate.getTimeInMillis()) {
        } else {
          date = startDate;
        }
      } else {
        date = startDate;
      }
    } else {
      if (startDate != null) {
        date = startDate;
      } else {
        if (endDate != null) {
          date = endDate;
        }
      }
    }
  }

  /**
     * 设置选中时间,默认选中当前时间
     */
  private void setTime() {
    int year;
    int month;
    int day;
    int hours;
    int minute;
    int seconds;
    Calendar calendar = Calendar.getInstance();
    if (date == null) {
      calendar.setTimeInMillis(System.currentTimeMillis());
      year = calendar.get(Calendar.YEAR);
      month = calendar.get(Calendar.MONTH);
      day = calendar.get(Calendar.DAY_OF_MONTH);
      hours = calendar.get(Calendar.HOUR_OF_DAY);
      minute = calendar.get(Calendar.MINUTE);
      seconds = calendar.get(Calendar.SECOND);
    } else {
      year = date.get(Calendar.YEAR);
      month = date.get(Calendar.MONTH);
      day = date.get(Calendar.DAY_OF_MONTH);
      hours = date.get(Calendar.HOUR_OF_DAY);
      minute = date.get(Calendar.MINUTE);
      seconds = date.get(Calendar.SECOND);
    }
    System.out.println("month:" + month);
    System.out.println("day:" + day);
    System.out.println("year:" + year);
    wheelTime.setPicker(year, month, day, hours, minute, seconds);
  }

  @Override public void onClick(View v) {
    String tag = (String) v.getTag();
    if (tag.equals(TAG_CANCEL)) {
      dismiss();
    } else {
      returnData(v);
    }
  }

  public void returnData(View v) {
    if (timeSelectListener != null) {
      try {
        Date date = WheelTime.dateFormat.parse(wheelTime.getTime());
        timeSelectListener.onTimeSelect(date, v);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    dismiss();
  }

  public interface OnTimeSelectListener {
    void onTimeSelect(Date date, View v);
  }

  @Override public boolean isDialog() {
    return isDialog;
  }
}