package com.doilio.shopifymemory.fragments.game


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.doilio.shopifymemory.adapters.GridViewAdapter
import com.doilio.shopifymemory.R
import com.doilio.shopifymemory.databinding.FragmentGameBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        setHasOptionsMenu(true)
        binding.lifecycleOwner = this

        val args = GameFragmentArgs.fromBundle(arguments!!)
        val viewModelFactory = GameViewModelFactory(args.pairs)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(GameViewModel::class.java)
        binding.viewModel = viewModel

        // Indicates what game mode we are using when we open this fragment
        when (args.pairs) {
            2 -> {
                showMessage("Game Mode: Match ${args.pairs}")
                gameLogicForModeTwo()
            }
            3 -> {
                showMessage("Game Mode: Match ${args.pairs}")
                gameLogicForModeThree()
            }
            4 -> {
                showMessage("Game Mode: Match ${args.pairs}")
                gameLogicForModeFour()
            }
        }

        viewModel.rightMoves.observe(this, Observer { rightMoves ->
            val wrongMoves = viewModel.wrongMoves.value!!
            val totalRightMoves = viewModel.totalRightMoves.value!!

            (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.game_fragment_title, rightMoves, totalRightMoves)

            if (rightMoves == totalRightMoves) {
                findNavController().navigate(
                    GameFragmentDirections.actionGameFragmentToWinnerFragment(
                        rightMoves,
                        wrongMoves,
                        args.pairs
                    )
                )
            }
        })

        return binding.root
    }

    /**
     * Game Logic for (Mode 4)
     * Gets the list that has been subset, and had it's items multiplied to fit this game Mode
     * Only compares the products after 4 items have been clicked
     */
    private fun gameLogicForModeFour() {
        viewModel.products.observe(this, Observer { products ->

            val adapter = GridViewAdapter(
                activity!!.applicationContext,
                products
            )
            binding.gridView.adapter = adapter

            var clicked = 0
            var firstClicked = -1L
            var secondClicked = -1L
            var thirdClicked = -1L
            var fourthClicked = -1L //Added

            binding.gridView.setOnItemClickListener { _, view, position, _ ->

                val product = products[position]
                val productImage = product.image.src
                val gridItemText = view.findViewById<TextView>(R.id.card_text)
                val gridItem = view.findViewById<ImageView>(R.id.product_image)


                if (gridItemText.text == "back") {
                    if (clicked < 4) { //Changed

                        when (clicked) {
                            0 -> {
                                firstClicked = product.id
                            }
                            1 -> {
                                secondClicked = product.id
                            }
                            2 -> {
                                thirdClicked = product.id
                            }
                            else -> {
                                fourthClicked = product.id //Added
                            }
                        }

                        Glide.with(view.context).load(productImage).into(gridItem)
                        clicked++
                        Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                        gridItemText.text = product.id.toString()
                        if (clicked == 4) { //Changed
                            // Comparar os 4 itens
                            if (firstClicked == secondClicked && secondClicked == thirdClicked && firstClicked == fourthClicked) {//Added
                                Timber.d("Same Items!")
                                viewModel.incrementRightMoves()
                                product.cardFace = true
                                clicked = 0

                            } else {
                                viewModel.incrementWrongMoves()
                                Timber.d("Different Items!")
                            }

                        }

                    } else {
                        showMessage("You can only open 4 cards!") //Changed
                    }
                } else {
                    Glide.with(view.context).load(R.drawable.slab_back).into(gridItem)
                    clicked--
                    Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                    gridItemText.text = getString(R.string.back)
                }

                Timber.d("Right Moves: ${viewModel.rightMoves.value}\nWrong Moves: ${viewModel.wrongMoves.value}\n")
            }
        })
    }

    /**
     * Game Logic for (Mode 3)
     * Gets the list that has been subset, and had it's items multiplied to fit this game Mode
     * Only compares the products after 3 items have been clicked
     */
    private fun gameLogicForModeThree() {
        viewModel.products.observe(this, Observer { products ->

            val adapter = GridViewAdapter(
                activity!!.applicationContext,
                products
            )
            binding.gridView.adapter = adapter

            var clicked = 0
            var firstClicked = -1L
            var secondClicked = -1L
            var thirdClicked = -1L

            binding.gridView.setOnItemClickListener { _, view, position, _ ->

                val product = products[position]
                val productImage = product.image.src
                val gridItemText = view.findViewById<TextView>(R.id.card_text)
                val gridItem = view.findViewById<ImageView>(R.id.product_image)

                // Logica para o jogo Mode 3
                if (gridItemText.text == "back") {
                    if (clicked < 3) {

                        when (clicked) {
                            0 -> {
                                firstClicked = product.id
                            }
                            1 -> {
                                secondClicked = product.id
                            }
                            else -> {
                                thirdClicked = product.id
                            }
                        }

                        Glide.with(view.context).load(productImage).into(gridItem)
                        clicked++
                        Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                        gridItemText.text = product.id.toString()
                        if (clicked == 3) {
                            // Comparar os 3 itens
                            if (firstClicked == secondClicked && secondClicked == thirdClicked) {
                                Timber.d("Same Items!")
                                viewModel.incrementRightMoves()
                                product.cardFace = true
                                clicked = 0

                            } else {
                                viewModel.incrementWrongMoves()
                                Timber.d("Different Items!")
                            }

                        }

                    } else {
                        showMessage("You can only open 3 cards!")
                    }
                } else {
                    Glide.with(view.context).load(R.drawable.slab_back).into(gridItem)
                    clicked--
                    Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                    gridItemText.text = getString(R.string.back)
                }

                Timber.d("Right Moves: ${viewModel.rightMoves.value}\nWrong Moves: ${viewModel.wrongMoves.value}\n")
            }
        })
    }

    /**
     * Game Logic for (Mode 2)
     * Gets the list that has been subset, and had it's items multiplied to fit this game Mode
     * Only compares the products after 2 items have been clicked
     */
    private fun gameLogicForModeTwo() {
        viewModel.products.observe(this, Observer { products ->

            val adapter = GridViewAdapter(
                activity!!.applicationContext,
                products
            )
            binding.gridView.adapter = adapter

            var clicked = 0
            var firstClicked = -1L
            var secondClicked = -1L

            binding.gridView.setOnItemClickListener { _, view, position, _ ->

                val product = products[position]
                val productImage = product.image.src
                val gridItemText = view.findViewById<TextView>(R.id.card_text)
                val gridItem = view.findViewById<ImageView>(R.id.product_image)


                // Logica para o jogo Mode 2
                if (gridItemText.text == "back") {
                    if (clicked < 2) {

                        if (clicked == 0) {
                            firstClicked = product.id
                        } else {
                            secondClicked = product.id
                        }
                        Glide.with(view.context).load(productImage).into(gridItem)
                        clicked++
                        Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                        gridItemText.text = product.id.toString()
                        if (clicked == 2) {
                            // Comparar os itens
                            if (firstClicked == secondClicked) {
                                Timber.d("Same Items!")
                                viewModel.incrementRightMoves()
                                product.cardFace = true
                                clicked = 0

                            } else {
                                viewModel.incrementWrongMoves()
                                Timber.d("Different Items!")
                            }

                        }

                    } else {
                        showMessage("You can only open 2 cards!")
                    }
                } else {
                    Glide.with(view.context).load(R.drawable.slab_back).into(gridItem)
                    clicked--
                    Timber.d("Clicked ${gridItemText.text}, Count $clicked  at position $position")
                    gridItemText.text = getString(R.string.back)
                }

                Timber.d("Right Moves: ${viewModel.rightMoves.value}\nWrong Moves: ${viewModel.wrongMoves.value}\n")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

}
