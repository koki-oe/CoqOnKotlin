package jp.waseda.fuji.kokioe.coqonkotlin

import jp.waseda.fuji.kokioe.coqonkotlin.CoqOnKotlin.Companion.coq

fun main() {
    coq {
        /**
         * From mathcomp
         * Require Import ssreflect div. */
        from("mathcomp") {
            import("ssreflect", "div")
        }

        /**
         * Section ModusPonens.
         * Variables X Y : Prop.
         *
         * Hypothesis XtoY_is_true : X -> Y.
         * Hypothesis X_is_true : X.
         *
         * Theorem MP : Y.
         * Proof.
         * move: X_is_true.
         * by [].
         * Qed.
         *
         * End ModusPonens. */
        section("ModusPonens") {
            val x = prop()
            val y = prop()

            hypothesis(x then y)
            val hypX = hypothesis(x)

            theorem("MP", y) { // Proof
                move(hypX)
                by()
            } // Qed
        }
        val mp = getTheorem("MP")

        /**
         * Section HilbertSAxiom.
         * Variables A B C : Prop.
         *
         * Theorem HS1 : (A -> (B -> C)) -> ((A -> B) -> (A -> C)).
         * Proof.
         * move=> AtoBtoC_is_true.
         * move=> AtoB_is_true.
         * move=> A_is_true.
         *
         * apply: (MP B C).
         *
         * apply: (MP A (B -> C)).
         * by [].
         * by [].
         *
         * apply: (MP A B).
         * by [].
         * by [].
         * Qed.
         *
         * End HilbertSAxiom.*/

        section("HilbertSAxiom") {
            val a = prop("A")
            val b = prop("B")
            val c = prop("C")

            // TODO: convert String "(A -> (B -> C)) -> ((A -> B) -> (A -> C))" to the 'then' statement.
            theorem("HS1", (a then (b then c)) then ((a then b) then (a then c))) {
                move("AtoBtoC_is_true")
                move("AtoB_is_true")
                move("A_is_true")

                apply(mp, b, c)
                apply(mp, a, b then c)
                by()
                by()

                apply(mp, a, b)
                by()
                by()
            }
        }
    }.toCoq().also { print(it) }

    coq {
        /**
         * From mathcomp
         *      Require Import ssreflect ssrnat.*/
        from("mathcomp") {
            import("ssreflect", "ssrnat")
        }

        /** Section naturalNumber. */
        section("naturalNumber") {
            /**
             * Lemma addOnEqn (n : nat ) : 0 + n = n.
             * Proof. by []. Qed.*/

            /* lemma("addOnEqn", { n: Natural -> 0 + n eq n }) {
                by()
            }*/

            /**
             * Lemma addn3Eq2n1 (n : nat) : n + 3 = 2 + n + 1.
             * Proof.
             * rewrite addn1.
             * rewrite add2n.
             * rewrite addnC.
             * by [].
             * Qed.*/


            /** Fixpoint sum n := if n is m.+1 then sum m + n else 0. */

            /**
             * Lemma sumGauss (n : nat) : sum n * 2 = (n + 1) * n.
             * Proof.
             * elim: n => [// | n IHn].
             * rewrite mulnC.
             * rewrite (_ : sum (n.+1) = n.+1 + (sum n)); last first.
             * rewrite /=.
             * by rewrite addnC.
             * rewrite mulnDr.
             * rewrite mulnC in IHn.
             * rewrite IHn.
             * rewrite 2!addn1.
             * rewrite [_ * n]mulnC.
             * rewrite -mulnDl.
             * by [].
             * Qed. */
        }
        /** End naturalNumber. */
    }.also { print(it.toCoq()) }

    /** Module System */
    coq {
        /** Module Mod. */

        /** Definition T := nat. */
        /** Check T. */

        /** End Mod. */

        /** Fail Check T. */

        /** Import Mod. */
        /** Check T. */
    }
}