package com.abs.javarepos.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.abs.javarepos.R
import com.abs.javarepos.model.Repo
import com.abs.javarepos.view.adapter.RepoAdapter
import com.abs.javarepos.view.util.FadeUtils
import com.abs.javarepos.viewmodel.ReposViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_repos.*

class ReposActivity : AppCompatActivity() {

    private lateinit var repoAdapter: RepoAdapter
    private lateinit var viewModel: ReposViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repos)

        supportActionBar?.title = getString(R.string.repos_title)

        repoAdapter = RepoAdapter(object :
            RepoAdapter.OnItemClickListener {
            override fun onItemClick(repo: Repo) {
                val intent = Intent(this@ReposActivity, PullRequestsActivity::class.java)
                intent.putExtra(PullRequestsActivity.REPO_KEY, repo)
                startActivity(intent)
            }
        })


        rvRepos.layoutManager = LinearLayoutManager(this)
        rvRepos.adapter = repoAdapter

        viewModel = ViewModelProviders.of(this).get(ReposViewModel::class.java)
        viewModel.repoPagedList.observe(this, Observer { pagedList ->
            pagedList?.let {
                FadeUtils.fadeOut(progressBar)
                repoAdapter.submitList(pagedList)
            }
        })

        viewModel.networkError.observe(this, Observer { hasNetworkError ->
            if (hasNetworkError) {
                Snackbar.make(clRepos, getString(R.string.error_get_repos), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.action_reload)) {
                        FadeUtils.fadeIn(progressBar)
                        viewModel.repoDataSource.value?.invalidate()
                    }
                    .show()
            }
        })
    }
}
