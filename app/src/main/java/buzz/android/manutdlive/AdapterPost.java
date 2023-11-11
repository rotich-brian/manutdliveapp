package buzz.android.manutdlive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int VIEW_TYPE_LOADING = 0;
    final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private ArrayList<ModelPost> postArrayList;
    private SelectListener listener;

    public AdapterPost(Context context, ArrayList<ModelPost> postArrayList, SelectListener listener) {
        this.context = context;
        this.postArrayList = postArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==VIEW_TYPE_ITEM) {

            View view = LayoutInflater.from(context).inflate(R.layout.post_ads_row, parent, false);
            return new HolderPost(view);

        }
        else if (viewType==3) {
            View view = LayoutInflater.from(context).inflate(R.layout.blog_post_headlines, parent, false);
            return new HolderPost(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.loading_ui, parent,false);
            return new LoadingUIHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderPost)
        startPopulate((HolderPost) holder, position);
    }

    private void startPopulate(HolderPost holder, int position) {
        ModelPost model = postArrayList.get(position);

        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        //converting Description HTML/text using jsoup
        Document document = Jsoup.parse(content);
        try {
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.drawable.ic_image_placeholder).into(holder.imageArt);
        } catch (Exception e) {
            holder.imageArt.setImageResource(R.drawable.ic_image_placeholder);
        }

        //format Date
        String gmtDate = published;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a"); //e.g. 23/10/2023 02:12 PM
        String formattedDate = "";


        try {
            Date date = dateFormat1.parse(gmtDate);
            formattedDate = dateFormat2.format(date);

        }catch (Exception e){
            formattedDate = published;
            e.printStackTrace();
        }

        String timeAgo=calculateTimeAgo(formattedDate);

        holder.txtTitle.setText(title);
        holder.txtDescription.setText(document.text());
        holder.txtPublishInfo.setText("By "+ authorName+ "     "+timeAgo);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                listener.OnPostClicked(postArrayList.get(position));
                holder.txtTitle.setTextColor(R.color.gray1);
            }
        });
    }

    private String calculateTimeAgo(String formattedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy K:mm a");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(formattedDate).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if (postArrayList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else
        if (position==0 || position%5==0) {
            return 3;
        }else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class HolderPost extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtPublishInfo;
        ImageView imageArt;
        ImageButton moreBtn;
        CardView cardView;

        public HolderPost(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtPublishInfo = itemView.findViewById(R.id.txtPublishInfo);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            imageArt = itemView.findViewById(R.id.imageArt);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            cardView = itemView.findViewById(R.id.main_container);
        }
    }

    class  LoadingUIHolder extends RecyclerView.ViewHolder{
        public LoadingUIHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
