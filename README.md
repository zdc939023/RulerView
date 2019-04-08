# RulerView

RulerView 继承自View，根据设计小姐姐需求，让用户在选择身高和体重时，通过滑动刻度尺来设置，为达到设计要求， 和好的用户体验，这里通过Canvas绘制，同时结合VelocityTracker和Scroller让用户滑动时有滑动缓冲的感觉，提示了用户选择的体验。
由于RulerView体量很小，只有一个Java类，就没有单独封装成一个Library，在使用时只需要把项目中的RulerView的java类copy到自己的项目中，根据需求自己定制使用即可，同时项目中还包含了横竖两种不同方向的刻度尺，方便你更好的去定制实现。

## 使用方法

#### 1，在配置文件中添加RulerView的引用

        <com.zdc.rulerview.RulerView
          android:id="@+id/rv_rulerView"
          android:layout_width="match_parent"/>
          
#### 2，在Activity中进行初始化
        RulerView rulerView=findViewById(R.id.rv_rulerView);
        //设置默认值
        rulerView.setValue(60);
        //设置刻度区间和显示模式
        rulerView.initViewParam(60,200,RulerView.MOD_TYPE_HALF);
        //添加滑动选中的刻度回调
        rulerView.setOnValueChangeListener(new RulerView.onValueChangeListener() {
            @Override
            public void valueChange(final float value) {
                //在这里得到滑动值的变化
            }
        });
#### 3，预览效果如下
  <img src="https://github.com/zdc212133/RulerView/blob/master/screenshot/Screenshot_20190408-115412.jpg">
  <img src="https://github.com/zdc212133/RulerView/blob/master/screenshot/Screenshot_20190408-115517.jpg">
