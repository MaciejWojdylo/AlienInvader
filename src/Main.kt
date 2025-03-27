import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.random.Random

const val WIDTH = 800.0
const val HEIGHT = 600.0
var shipPosition = WIDTH / 2
var score = 0
var destroyedEnemies = 0  // Licznik zniszczonych obcych

class AlienInvaderApp : Application() {

    private val ship = Rectangle(50.0, 10.0, Color.BLUE)
    private val aliens = mutableListOf<Rectangle>()
    private val projectiles = mutableListOf<Rectangle>()
    private val alienSpeed = 2.0
    private val projectileSpeed = 5.0

    override fun start(primaryStage: Stage) {
        val root = Pane()

        // Ustawienie statku
        ship.x = shipPosition - ship.width / 2
        ship.y = HEIGHT - ship.height - 20

        root.children.add(ship)

        val timeline = Timeline(KeyFrame(Duration.millis(20.0), {
            moveAliens()
            moveProjectiles()
            checkCollisions(root)
            spawnAliens(root)
        }))
        timeline.cycleCount = Timeline.INDEFINITE
        timeline.play()

        val scene = Scene(root, WIDTH, HEIGHT, Color.BLACK)

        scene.setOnKeyPressed { event ->
            when (event.code) {
                KeyCode.A -> if (ship.x > 0) ship.x -= 5
                KeyCode.D -> if (ship.x < WIDTH - ship.width) ship.x += 5
                KeyCode.Q -> System.exit(0)
                KeyCode.SPACE -> shootProjectile(root)
                else -> {}
            }
        }

        primaryStage.title = "Alien Invader Game"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun moveAliens() {
        // Przemieszczanie obcych
        for (alien in aliens) {
            alien.y += alienSpeed
            if (alien.y > HEIGHT) {
                aliens.remove(alien)
            }
        }
    }

    private fun moveProjectiles() {
        // Przemieszczanie pocisków
        for (projectile in projectiles) {
            projectile.y -= projectileSpeed
            if (projectile.y < 0) {
                projectiles.remove(projectile)
            }
        }
    }

    private fun shootProjectile(root: Pane) {
        // Tworzenie nowego pocisku
        val projectile = Rectangle(5.0, 10.0, Color.YELLOW)
        projectile.x = ship.x + ship.width / 2 - projectile.width / 2
        projectile.y = ship.y
        projectiles.add(projectile)
        root.children.add(projectile)
    }

    private fun spawnAliens(root: Pane) {
        // Spawnowanie nowych obcych
        if (Random.nextInt(0, 100) == 0) {
            val alien = Rectangle(30.0, 20.0, Color.RED)
            alien.x = Random.nextDouble(0.0, WIDTH - alien.width)
            alien.y = 0.0

            aliens.add(alien)
            root.children.add(alien)
        }
    }

    private fun checkCollisions(root: Pane) {
        // Sprawdzanie kolizji pocisków z obcymi
        val iterator = aliens.iterator()
        while (iterator.hasNext()) {
            val alien = iterator.next()
            val projectileIterator = projectiles.iterator()

            while (projectileIterator.hasNext()) {
                val projectile = projectileIterator.next()
                if (alien.boundsInParent.intersects(projectile.boundsInParent)) {
                    iterator.remove()
                    projectileIterator.remove()

                    root.children.remove(alien)
                    root.children.remove(projectile)

                    destroyedEnemies++
                    println("Zniszczono obcego! Zniszczonych obcych: $destroyedEnemies")
                    break
                }
            }
        }
    }
}

fun main() {
    Application.launch(AlienInvaderApp::class.java)
}
