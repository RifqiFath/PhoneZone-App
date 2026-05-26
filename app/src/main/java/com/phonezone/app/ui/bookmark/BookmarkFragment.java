package com.phonezone.app.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.phonezone.app.R;
import com.phonezone.app.adapters.BookmarkAdapter;
import com.phonezone.app.models.BookmarkItem;
import com.phonezone.app.ui.detail.DetailActivity;
import com.phonezone.app.utils.AppPreferences;

import java.util.List;

public class BookmarkFragment extends Fragment {

    private RecyclerView rvBookmarks;
    private BookmarkAdapter bookmarkAdapter;
    private View layoutEmpty;
    private TextView tvCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadBookmarks();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookmarks when returning to this fragment
        loadBookmarks();
    }

    private void initViews(View view) {
        rvBookmarks = view.findViewById(R.id.rv_bookmarks);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvCount = view.findViewById(R.id.tv_bookmark_count);

        view.findViewById(R.id.btn_clear_all).setOnClickListener(v -> {
            showClearAllDialog();
        });
    }

    private void loadBookmarks() {
        List<BookmarkItem> bookmarks = AppPreferences.getInstance(requireContext()).getBookmarks();

        if (bookmarks.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvBookmarks.setVisibility(View.GONE);
            tvCount.setText("0 tersimpan");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvBookmarks.setVisibility(View.VISIBLE);
            tvCount.setText(bookmarks.size() + " tersimpan");

            bookmarkAdapter = new BookmarkAdapter(requireContext(), bookmarks,
                    // Click item
                    item -> {
                        Intent intent = new Intent(requireContext(), DetailActivity.class);
                        intent.putExtra(DetailActivity.EXTRA_ARTICLE_ID, item.getArticleId());
                        intent.putExtra(DetailActivity.EXTRA_ARTICLE_TITLE, item.getTitle());
                        intent.putExtra(DetailActivity.EXTRA_ARTICLE_IMAGE, item.getImageUrl());
                        startActivity(intent);
                    },
                    // Delete item
                    item -> {
                        AppPreferences.getInstance(requireContext()).removeBookmark(item.getArticleId());
                        loadBookmarks();
                    }
            );
            rvBookmarks.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvBookmarks.setAdapter(bookmarkAdapter);
        }
    }

    private void showClearAllDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Semua Bookmark")
                .setMessage("Apakah kamu yakin ingin menghapus semua artikel yang disimpan?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    AppPreferences.getInstance(requireContext()).clearBookmarks();
                    loadBookmarks();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
