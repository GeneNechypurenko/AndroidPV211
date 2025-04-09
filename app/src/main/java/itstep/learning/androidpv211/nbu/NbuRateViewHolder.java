package itstep.learning.androidpv211.nbu;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import itstep.learning.androidpv211.R;
import itstep.learning.androidpv211.orm.NbuRate;

public class NbuRateViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvTxt;
    private final TextView tvCc;
    private final TextView tvRate;
    private final TextView tvCcRev;
    private final TextView tvRateRev;
    private NbuRate nbuRate;

    public NbuRateViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTxt = itemView.findViewById(R.id.nbu_rate_txt);
        tvCc =  itemView.findViewById(R.id.nbu_rate_cc);
        tvRate = itemView.findViewById(R.id.nbu_rate_rate);
        tvCcRev =  itemView.findViewById(R.id.nbu_rate_cc_rev);
        tvRateRev = itemView.findViewById(R.id.nbu_rate_rate_rev);
    }

    private void showData() {
        String ccStr = "1 " + nbuRate.getCc() + " = ";
        String rateStr = nbuRate.getRate() + " UAH";
        String ccRevStr = "1 UAH = ";
        String rateRevStr = String.format("%.4f %s", 1 / nbuRate.getRate(), nbuRate.getCc());

        tvTxt.setText(nbuRate.getTxt());
        tvCc.setText(ccStr);
        tvRate.setText(rateStr);
        tvCcRev.setText(ccRevStr);
        tvRateRev.setText(rateRevStr);
    }

    public NbuRate getNbuRate() {
        return nbuRate;
    }

    public void setNbuRate(NbuRate nbuRate) {
        this.nbuRate = nbuRate;
        showData();
    }
}
