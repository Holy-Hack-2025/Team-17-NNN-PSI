package holyhack.eldermind;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find views
        TextView textView = view.findViewById(R.id.home_text);
        Button button = view.findViewById(R.id.home_button);

        // Set button click action
        button.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Button Clicked!", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
