package com.raulpineres.consumoapirxjava

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.raulpineres.consumoapirxjava.api.ArticlesApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val client by lazy { ArticlesApiClient.create() }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        showArticles()

        image_search.setOnClickListener {
            if (!edit_text.text.toString().equals("")){
                showArticle(edit_text.text.toString().toInt())
                progress_bar.visibility = View.GONE
            }else{
                Snackbar.make(image_search, "Debes ingresar el id del artículo a buscar", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showArticles(){

        progress_bar.visibility = View.VISIBLE

        disposable = client.getArticlesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result -> progress_bar.visibility = View.GONE
                              text_view_result.text = result.toString()
                },{
                    error -> Toast.makeText(this, "Ocurrió un error al consumir servicio", Toast.LENGTH_LONG).show()
                } )
    }

    private fun showArticle(id: Int){

        progress_bar.visibility = View.VISIBLE

        disposable = client.getArticle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result -> text_view_result.text = result.toString()
                },{
                    error -> Toast.makeText(this, "Ocurrió un error obteniendo el registro con el id: ${id}", Toast.LENGTH_LONG).show()
                })
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
