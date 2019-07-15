package To.Do.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListView extends ListView {

    public CustomListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    //This is so that the ListView doesn't fade out at the top, only the bottom
    @Override
    protected float getTopFadingEdgeStrength() {
        return 0;
    }
}
