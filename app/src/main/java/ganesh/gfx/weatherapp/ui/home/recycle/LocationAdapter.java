package ganesh.gfx.weatherapp.ui.home.recycle;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ganesh.gfx.weatherapp.R;
import ganesh.gfx.weatherapp.ui.home.HomeFragment;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.RecycleViewHolder> implements View.OnClickListener{

    List<Address> address;
    Context context;
    private ItemClickListener clickListener;

    public LocationAdapter(List<Address> address){
        this.address = address;
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.location_item, parent, false);
        LocationAdapter.RecycleViewHolder viewHolder = new RecycleViewHolder(listItem);
        context = parent.getContext();
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        String add = " ";
        add += address.get(position).getAddressLine(0);
        holder.location.setText(add);
    }

    @Override
    public int getItemCount() {
        return address.size();
    }

    @Override
    public void onClick(View view) {

    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    class RecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public Button location;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.location_btn);
            location.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, address.get(getAdapterPosition()));
        }
    }

}
