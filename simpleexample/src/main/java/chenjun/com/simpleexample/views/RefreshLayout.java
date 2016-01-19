package chenjun.com.simpleexample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import chenjun.com.simpleexample.R;
import chenjun.com.simpleexample.listener.OnRefreshListener;

/**
 * Created by chenjun on 16-1-15.
 */
public class RefreshLayout  extends ViewGroup {
    private RefreshView m_listView;
    private View m_headview;
    private TextView headView_status_str;
    private ProgressBar headView_progressBar;

    private Context m_context;

    private int headViewHeight=0;
    private int pullDownDistance=0;//移动过程y轴的变化值
    private float lastX;//每次事件down动作 x坐标
    private float lastY;//每次事件down动作 y坐标
    private Scroller mScroller;
    private OnRefreshListener m_onRefreshListener;

    public RefreshLayout(Context context) {
        super(context);
        m_context=context;
        intiLayout();
    }
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context=context;
        intiLayout();
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        m_context=context;
        intiLayout();
    }

    public void addOnRefreshLister(OnRefreshListener onRefreshListener){
       m_onRefreshListener = onRefreshListener;
    }

    private void intiLayout() {
        mScroller = new Scroller(m_context);
        m_headview= LayoutInflater.from(m_context).inflate(R.layout.headview, null);
        m_listView = new RefreshView(m_context);

        initHeadView();

        LayoutParams l1 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        LayoutParams l2 = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

        m_headview.setLayoutParams(l1);
        m_listView.setLayoutParams(l2);

        this.addView(m_headview);
        this.addView(m_listView);
    }

    private void initHeadView() {
        headView_status_str = (TextView) m_headview.findViewById(R.id.status_str);
        headView_progressBar= (ProgressBar) m_headview.findViewById(R.id.progressbar);
    }

    public RefreshView getRefreshView(){
        return m_listView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        headViewHeight = m_headview.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean b, int l, int t, int r, int bottom) {
//        m_headview.layout(0, -headViewHeight + pullDownDistance, m_headview.getMeasuredWidth(), pullDownDistance);
//        m_listView.layout(0, pullDownDistance, m_listView.getMeasuredWidth(), m_listView.getMeasuredHeight() + pullDownDistance);
        Log.d("test","m_headview"+m_headview.getTop());
        m_headview.layout(0, -headViewHeight , m_headview.getMeasuredWidth(), 0);
        m_listView.layout(0, 0, m_listView.getMeasuredWidth(), m_listView.getMeasuredHeight());
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=event.getX();
                lastY=event.getY();
                oldX= (int) event.getX();
                oldY= (int) event.getY();
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    return  true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                int position = m_listView.getFirstVisiblePosition();
                Log.d("simple", "getFirstVisiblePosition====" + position);
                Log.d("simple", "m_listView.getTop()====" + m_listView.getTop());
                float nowX=event.getX();
                float nowY=event.getY();
                float deltaX=nowX-lastX;
                float deltaY=nowY-lastY;
                if(Math.abs(deltaX)>Math.abs(deltaY)){
                    return false;
                }else{
                    if(m_listView.getTop()>0){
                        return true;
                    }else{
                        if(position==0&&deltaY>0){
                            return true;
                        }else{
                            return false;
                        }
                    }

                }

            case MotionEvent.ACTION_UP:
                return false;

        }
        return false;
    }
    int oldX;
    int oldY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x= (int) event.getX();
        int y= (int) event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                    return  true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float nowX=event.getX();
                float nowY=event.getY();
                float deltaX=nowX-lastX;
                float deltaY=nowY-lastY;

                int xx=(int)nowX-oldX;
                int yy=(int)nowY-oldY;
                Log.d("test","oldY===="+oldY);
                Log.d("test","yy===="+yy);

                if(Math.abs(deltaX)>Math.abs(deltaY)){
                    return true;
                }else{
                    pullDownDistance=(int)deltaY;
                }
                if(pullDownDistance>headViewHeight){
                    notifyHeadViewReleaseRefresh();
                }else{
                    if(pullDownDistance>0){
                        notifyHeadViewpullRefresh();

                    }else{
                        pullDownDistance=0;
                       // requestLayout();
                        scrollTo(0,0);
                        return true;
                    }

                }
                //requestLayout();
                scrollBy(0,-yy);
                break;
            case MotionEvent.ACTION_UP:
                if(pullDownDistance>=headViewHeight){
                    pullDownDistance=headViewHeight;
                    notifyHeadViewRefreshing();
                }else{
                    pullDownDistance=0;
                }

              // requestLayout();
                mScroller.startScroll(0,getScrollY(),0,-pullDownDistance-getScrollY(),500);
                invalidate();
             default:
                    break;

        }
        Log.d("test","y===="+y);
        oldX = x;
        oldY = y;
        return true;
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }
    }

    private void notifyHeadViewpullRefresh() {
        headView_status_str.setText("下拉刷新");
        headView_progressBar.setVisibility(View.INVISIBLE);
    }

    private void notifyHeadViewReleaseRefresh() {
        headView_status_str.setText("松开刷新");
        headView_progressBar.setVisibility(View.INVISIBLE);
    }

    private void notifyHeadViewRefreshing() {
        headView_status_str.setText("正在刷新");
        headView_progressBar.setVisibility(View.VISIBLE);
        if(m_onRefreshListener!=null){
            m_onRefreshListener.onRefreshing();
        }
    }

    /**
     * if onRefreshing  manual stop
     */
    public void stopRefreshing(){
        pullDownDistance=0;
        if(!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
       scrollTo(0,0);
    }
}
